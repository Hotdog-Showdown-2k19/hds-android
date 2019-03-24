package com.github.akassharjun.hotdogshowdown

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_final.*
import kotlinx.android.synthetic.main.activity_main.*

class FinalActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var documentID = ""
    private var amount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        val newAmount = intent.getStringExtra("newAmount")
        val previousAmount = intent.getStringExtra("previousAmount")
        val userID = intent.getStringExtra("userID")
        documentID = intent.getStringExtra("documentID")

        calculateTotalHotdogs(userID, previousAmount, newAmount)

        mTotal.text = amount.toString()
        mCurrentRound.text = newAmount

        mNext.setOnClickListener {
            val intent = Intent(this@FinalActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun calculateTotalHotdogs(userID: String, previousHotdogs: String, newAmount: String) {
        val TAG = "ONSUBMIT"
        val collectionName = "Total Hotdog Count"
        db.collection(collectionName).whereEqualTo("userID", userID).get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        updateTotalCollection(userID, newAmount)
                        amount = mNumberOfHotdogs.text.toString().toInt()
                    } else {
                        val document = documents.documents[0]
                        amount = document["Total Hotdogs Eaten"].toString().toInt() + newAmount.toInt() - previousHotdogs.toInt()
                        updateTotalCollection(userID, amount.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
    }

    private fun updateTotalCollection(userID: String, amount: String) {
        val collectionReference = db.collection("Total Hotdog Count")

        val map = HashMap<String, String>()
        map["userID"] = userID
        map["Total Hotdogs Eaten"] = amount

        collectionReference.document(documentID).set(map)
    }
}
