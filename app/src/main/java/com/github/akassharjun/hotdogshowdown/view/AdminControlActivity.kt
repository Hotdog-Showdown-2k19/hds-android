package com.github.akassharjun.hotdogshowdown.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.akassharjun.hotdogshowdown.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_admin_control.*

class AdminControlActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_control)

        mReset.setOnClickListener {
            showConfirmationDialog()
        }

        exit.setOnClickListener {
            val intent = Intent(this@AdminControlActivity, VerificationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun showConfirmationDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val confirmationDialog = android.app.AlertDialog.Builder(this@AdminControlActivity).create()
        confirmationDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val positive = view.findViewById<Button>(R.id.positive)
        val negative = view.findViewById<Button>(R.id.negative)
        val message = view.findViewById<TextView>(R.id.message)

        message.text = "Are you sure that you want to reset the data? This action is irreversible!"

        positive.setOnClickListener {
            resetDB()
            confirmationDialog.dismiss()
        }

        negative.setOnClickListener {
            confirmationDialog.dismiss()
        }

        confirmationDialog.setCancelable(false)
        confirmationDialog.setView(view)
        confirmationDialog.show()
    }

    private fun resetDB() {
        database = FirebaseDatabase.getInstance().reference
        for (num in 1..20) {
            if (num < 10) {
                database.child("table_0$num").setValue("0")
            } else {
                database.child("table_$num").setValue("0")
            }
        }
    }
}