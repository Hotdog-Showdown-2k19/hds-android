package com.github.akassharjun.hotdogshowdown.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.akassharjun.hotdogshowdown.R
import com.github.akassharjun.hotdogshowdown.presenter.ResultActivityPresenter
import kotlinx.android.synthetic.main.activity_final.*

class ResultActivity : AppCompatActivity(), ResultActivityPresenter.View {

    private val presenter = ResultActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        val newAmount = intent.getStringExtra("newAmount").toInt()
        val previousAmount = intent.getStringExtra("previousAmount").toInt()
        val userID = intent.getStringExtra("userID")
        val documentID = intent.getStringExtra("documentID")

        mCurrentRound.text = newAmount.toString()

        val totalHotdogs = presenter.getPreviousHotdogs()

        mTotal.text = totalHotdogs.toString()

        mNext.setOnClickListener {
            val intent = Intent(this@ResultActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}
