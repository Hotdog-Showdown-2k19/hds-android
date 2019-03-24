package com.github.akassharjun.hotdogshowdown.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.beust.klaxon.Klaxon
import com.github.akassharjun.hotdogshowdown.R
import com.github.akassharjun.hotdogshowdown.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /* Variables */
    private var documentID = ""
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
        documentID = intent.getStringExtra("documentID")

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

        mReset.setOnClickListener {
            showConfirmationDialog("reset")
            mNumberOfHotdogs.text = "0"
            previousHotdogs = "0"
            updateCollection(mRoundName.text.toString(), "0")
        }

        mSubmit.setOnClickListener {
            showConfirmationDialog("submit")
        }

        mRoundName.setOnClickListener {
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

    private fun showConfirmationDialog(type: String) {
        val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val confirmationDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        confirmationDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val positive = view.findViewById<Button>(R.id.positive)
        val negative = view.findViewById<Button>(R.id.negative)
        val message = view.findViewById<TextView>(R.id.message)

        if (type == "submit") {
            message.text = "Are you sure that you want to submit the data?"
        } else {
            message.text = "Are you sure that you want to reset the data?"
        }

        positive.setOnClickListener {
            if (type == "submit") {
                val intent = Intent(this@MainActivity, FinalActivity::class.java)
                intent.putExtra("newAmount", mNumberOfHotdogs.text.toString())
                intent.putExtra("previousAmount", previousHotdogs)
                intent.putExtra("userID", userID)
                intent.putExtra("documentID", documentID)
                startActivity(intent)
            } else {
                reset()
            }
            confirmationDialog.dismiss()
        }

        negative.setOnClickListener {
            confirmationDialog.dismiss()
        }

        confirmationDialog.setCancelable(false)
        confirmationDialog.setView(view)
        confirmationDialog.show()
    }

    private fun reset() {
        mNumberOfHotdogs.text = "0"
        updateCollection(mRoundName.text.toString(), "0")
    }

    private fun showRoundSelectionDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_round_selection, null)
        val roundSelectionDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        roundSelectionDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val preliminaryRound = view.findViewById<Button>(R.id.preliminaryRound)
        val finalRound = view.findViewById<Button>(R.id.finalRound)

        preliminaryRound.setOnClickListener {
            mRoundName.text = getString(R.string.preliminary_round)
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

        collectionReference.document(documentID).set(map)
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
}



