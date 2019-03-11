package com.github.akassharjun.hotdogshowdown

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

    /* Variables */
    private var userDocumentID = ""
    private val db = FirebaseFirestore.getInstance()
    private var userID = ""
    private var previousHotdogs = "0"

    /* Lifecycle */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        val user = Klaxon().parse<User>(intent.getStringExtra("user"))!!

        // Setting user info
        val name = "${user.firstName}\n${user.lastName}"
        mName.text = name
        mId.text = user.id
        userID = user.id
        userDocumentID = intent.getStringExtra("userDocumentID")

        // onClick listeners
        mAddHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten++
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                updateCollection(mRoundName.text.toString(), hotdogsEaten.toString())
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        mRemoveHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten--
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                updateCollection(mRoundName.text.toString(), hotdogsEaten.toString())
            } else {
                Toast.makeText(this, "There are no hotdogs to be removed!", Toast.LENGTH_SHORT).show()
            }
        }

        mReset.setOnClickListener { mNumberOfHotdogs.text = "0" }

        mSubmit.setOnClickListener {
            onSubmit()
        }

        mRoundName.setOnClickListener {
            mNumberOfHotdogs.text = "0"
            showRoundSelectionDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        showRoundSelectionDialog()
    }

    /* Functions */
    private fun isAmountValid(hotdogsEaten: Int): Boolean {
        return hotdogsEaten > -1 && hotdogsEaten < 21
    }

    private fun showRoundSelectionDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_round_selection, null)
        val roundSelectionDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        roundSelectionDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val preliminaryRound = view.findViewById<Button>(R.id.preliminaryRound)
        val eatOffRound = view.findViewById<Button>(R.id.eatOffRound)
        val finalRound = view.findViewById<Button>(R.id.finalRound)

        preliminaryRound.setOnClickListener {
            mRoundName.text = getString(R.string.preliminary_round)
            getPreviousData(mRoundName.text.toString())
            roundSelectionDialog.dismiss()
        }

        eatOffRound.setOnClickListener {
            mRoundName.text = getString(R.string.eatoff_round)
            getPreviousData(mRoundName.text.toString())
            roundSelectionDialog.dismiss()
        }

        finalRound.setOnClickListener {
            mRoundName.text = getString(R.string.final_round)
            getPreviousData(mRoundName.text.toString())
            roundSelectionDialog.dismiss()
        }


        roundSelectionDialog.setCancelable(false)
        roundSelectionDialog.setView(view)
        roundSelectionDialog.show()
    }

    private fun updateCollection(collectionName: String, amount: String) {
        val collectionReference = db.collection(collectionName)

        val map = HashMap<String, String>()
        map["userID"] = userID
        map["Total Hotdogs Eaten"] = amount

        collectionReference.document(userDocumentID).set(map)
    }

    private fun onSubmit() {
        val TAG = "ONSUBMIT"
        val collectionName = "Total Hotdog Count"
        var amount: Int
        db.collection(collectionName).whereEqualTo("userID", userID).get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        updateCollection(collectionName, mNumberOfHotdogs.text.toString())
                        amount = mNumberOfHotdogs.text.toString().toInt()
                    } else {
                        val document = documents.documents[0]
                        amount = document["Total Hotdogs Eaten"].toString().toInt() + mNumberOfHotdogs.text.toString().toInt() - previousHotdogs.toInt()
                        updateCollection(collectionName, amount.toString())
                    }
                    // Passing values to final activity.
                    val intent = Intent(this@MainActivity, FinalActivity::class.java)
                    intent.putExtra("totalAmount", amount.toString())
                    intent.putExtra("amount", mNumberOfHotdogs.text.toString())
                    startActivity(intent)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
    }

    private fun getPreviousData(collectionName: String) {
        val TAG = "GET PREVIOUS DATA"
        db.collection(collectionName).whereEqualTo("userID", userID).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        mNumberOfHotdogs.text = document["Total Hotdogs Eaten"].toString()
                        previousHotdogs = document["Total Hotdogs Eaten"].toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
    }

    private fun debug() {
        val message = "Object ID : $userDocumentID \n User ID : $userID \n Round Name : ${mRoundName.text}"
        Log.d("HGS", message)
    }
}



