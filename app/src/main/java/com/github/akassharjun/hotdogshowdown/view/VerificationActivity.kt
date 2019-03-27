package com.github.akassharjun.hotdogshowdown.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.akassharjun.hotdogshowdown.R
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.dialog_invalid_user.view.*

class VerificationActivity : AppCompatActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if ((ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith("android.webkit.")) {
            val screenCoordinates = IntArray(2)
            view.getLocationOnScreen(screenCoordinates)
            val x = ev.rawX + view.left - screenCoordinates[0]
            val y = ev.rawY + view.top - screenCoordinates[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom) {
                try {
                    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this.window.decorView.applicationWindowToken, 0)
                } catch (e: NullPointerException) {
                    //pass
                }

            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        startActivity(Intent(this@VerificationActivity, LoginActivity::class.java))

        mPasscode.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                verifyPasscode()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        mNext.setOnClickListener {
            verifyPasscode()
        }
    }

    private fun verifyPasscode() {
        if (mPasscode.text.toString() == "abcd#1234" || mPasscode.text.toString() == "1234#abcd") {
            startActivity(Intent(this@VerificationActivity, LoginActivity::class.java))
        } else {
            showErrorDialog()
        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = this.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showErrorDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_invalid_user, null)
        val errorDialog = android.app.AlertDialog.Builder(this@VerificationActivity).create()
        errorDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))


        view.message.text = "Incorrect Passcode!\nTry again"
        errorDialog.setCancelable(false)
        errorDialog.setView(view)
        errorDialog.show()


        object : CountDownTimer(2000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                // TODO Auto-generated method stub

            }

            override fun onFinish() {
                // TODO Auto-generated method stub

                errorDialog.dismiss()
            }
        }.start()
    }
}