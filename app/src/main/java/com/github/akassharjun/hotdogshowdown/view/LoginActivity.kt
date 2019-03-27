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
import com.github.akassharjun.hotdogshowdown.presenter.LoginActivityPresenter
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), LoginActivityPresenter.View {

    private val presenter = LoginActivityPresenter(this)

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
        setContentView(R.layout.activity_login)

        mUserID.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                updateViews(View.GONE)
                presenter.retrieveUserFromDatabase(mUserID.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        mLogin.setOnClickListener {
            updateViews(View.GONE)
            presenter.retrieveUserFromDatabase(mUserID.text.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@LoginActivity, VerificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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

    override fun showErrorDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_invalid_user, null)
        val errorDialog = android.app.AlertDialog.Builder(this@LoginActivity).create()
        errorDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        errorDialog.setCancelable(false)
        errorDialog.setView(view)
        errorDialog.show()

        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                errorDialog.dismiss()
            }
        }.start()
    }

    override fun updateViews(visibility: Int) {
        mUserID.visibility = visibility
        mLogin.visibility = visibility
        mLabel.visibility = visibility
        if (visibility == View.VISIBLE) {
            mProgressBar.visibility = View.GONE
        } else {
            mProgressBar.visibility = View.VISIBLE
        }
    }

    override fun nextActivity(id: String, fName: String, lName: String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("firstName", fName)
        intent.putExtra("lastName", lName)
        startActivity(intent)
    }


}
