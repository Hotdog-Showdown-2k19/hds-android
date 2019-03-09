package com.github.akassharjun.hotdogshowdown

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.beust.klaxon.Klaxon
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var objectID = ""
    var db = FirebaseFirestore.getInstance()
    var userID = ""
    var previousHotdogs = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        val user = Klaxon().parse<User>(intent.getStringExtra("user"))

        mName.text = "${user?.firstName}\n${user?.lastName}"
        mId.text = user?.id

        userID = user?.id!!

        objectID = intent.getStringExtra("objectID")

        mAddHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten++
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                updateDatabase(mRoundName.text.toString(), hotdogsEaten.toString())
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        mRemoveHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten--
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                updateDatabase(mRoundName.text.toString(), hotdogsEaten.toString())
            } else {
                Toast.makeText(this, "There are no hotdogs to be removed!", Toast.LENGTH_SHORT).show()
            }
        }

        mReset.setOnClickListener { mNumberOfHotdogs.text = "0" }

        mSubmit.setOnClickListener {
            onSubmit()
        }

        mRoundName.setOnClickListener {
            showRoundSelectionDialog()
        }
    }

    private fun isAmountValid(hotdogsEaten: Int): Boolean {
        return hotdogsEaten > -1 && hotdogsEaten < 21
    }

    override fun onStart() {
        super.onStart()
        showRoundSelectionDialog()
    }


    private fun showRoundSelectionDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_round_selection, null)
        val roundSelectionDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        roundSelectionDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val preliminaryRound = view.findViewById<Button>(R.id.preliminaryRound)
        val eatOffRound = view.findViewById<Button>(R.id.eatOffRound)
        val finalRound = view.findViewById<Button>(R.id.finalRound)

        preliminaryRound.setOnClickListener {
            mRoundName.text = "Preliminary Round"
            getPreviousData(mRoundName.text.toString())
            roundSelectionDialog.dismiss()
        }

        eatOffRound.setOnClickListener {
            mRoundName.text = "Eatoff Round"
            getPreviousData(mRoundName.text.toString())
            roundSelectionDialog.dismiss()
        }

        finalRound.setOnClickListener {
            mRoundName.text = "Final Round"
            getPreviousData(mRoundName.text.toString())
            roundSelectionDialog.dismiss()
        }


        roundSelectionDialog.setCancelable(false)
        roundSelectionDialog.setView(view)
        roundSelectionDialog.show()
    }

    private fun updateDatabase(collectionName: String, amount: String) {
        val roundCollection = db.collection(collectionName)

        val map = HashMap<String, String>()
        map["userID"] = userID
        map["Total Hotdogs Eaten"] = amount

        roundCollection.document(objectID).set(map)
    }

    fun onSubmit() {
        val TAG = "ONSUBMIT"
        val COLLECTION_NAME = "Total Hotdogs Eaten"
        var amount = 0
        db.collection(COLLECTION_NAME).whereEqualTo("userID", userID).get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        updateDatabase(COLLECTION_NAME, mNumberOfHotdogs.text.toString())
                        amount = mNumberOfHotdogs.text.toString().toInt()
                    } else {
                        for (document in documents) {
                            amount = document["Total Hotdogs Eaten"].toString().toInt() + mNumberOfHotdogs.text.toString().toInt() - previousHotdogs.toInt()
                            Log.d("PREVIOUS", previousHotdogs)
                            Log.d("AMOUNT", amount.toString())
                            updateDatabase(COLLECTION_NAME, amount.toString())
                        }
                    }
                    val intent = Intent(this@MainActivity, FinalActivity::class.java)
                    intent.putExtra("totalAmount", amount.toString())
                    intent.putExtra("amount", mNumberOfHotdogs.text.toString())
                    startActivity(intent)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
    }

    fun getPreviousData(collectionName: String) {
        val TAG = "GET PREVIOUS DATA"
        db.collection(collectionName).whereEqualTo("userID", userID).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        mNumberOfHotdogs.text = document["Total Hotdogs Eaten"].toString()
                        previousHotdogs = mNumberOfHotdogs.text.toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
    }
}



