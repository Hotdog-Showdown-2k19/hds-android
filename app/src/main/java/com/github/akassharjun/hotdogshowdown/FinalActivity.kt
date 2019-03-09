package com.github.akassharjun.hotdogshowdown

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_final.*

class FinalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        val lastRound = intent.getStringExtra("amount")
        val total = intent.getStringExtra("totalAmount")

        mTest.text = "LAST ROUND : $lastRound \n TOTAL : $total"

        mNext.setOnClickListener {
            val intent = Intent(this@FinalActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
