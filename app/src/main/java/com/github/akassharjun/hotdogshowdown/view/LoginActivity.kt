package com.github.akassharjun.hotdogshowdown.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import com.github.akassharjun.hotdogshowdown.R
import com.github.akassharjun.hotdogshowdown.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

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

    fun createUsers() {
        val users = db.collection("Users")

        val userOne = User("HDS#001", "Akassharjun", "Shanmugarajah", "akassharjun@gmail.com", "0713836450", "Male", Date().time)

        val userTwo = User("HDS#002", "Visal", "Rajapakse", "visalrajapakse@gmail.com", "0777734578", "Male", Date().time)

        val userThree = User("HDS#003", "Dinuka", "Piyadigama", "dinukapiliyagama@gmail.com", "0723423948", "Male", Date().time)

        val userFour = User("HDS#004", "Archana", "", "archana@gmail.com", "0713323094", "Female", Date().time)

        val userFive = User("HDS#005", "Shirendra", null, "shirendra@gmail.com", "0713323094", "Male", Date().time)


        users.document().set(userFive)


        Log.d("NEWUSER", userOne.toString())

//        users.document("HDG#001").set(newUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        createUsers()

        mUserID.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                updateViews(View.GONE)
                retrieveUserFromDatabase()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        mLogin.setOnClickListener {
            updateViews(View.GONE)
            retrieveUserFromDatabase()
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

    private fun retrieveUserFromDatabase() {
        val TAG = "GETUSERS"
        db.collection("Users").whereEqualTo("id", mUserID.text.toString()).get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        showErrorDialog()
                        updateViews(View.VISIBLE)
                    } else {
                        for (document in documents) {
                            Log.d(TAG, document.data.toString().replace("=", ":", true))
                            val user = document.toObject(User::class.java)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("user", Klaxon().toJsonString(user))
                            intent.putExtra("documentID", document.id)
                            startActivity(intent)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
    }

    private fun updateViews(visibility: Int) {
        mUserID.visibility = visibility
        mLogin.visibility = visibility
        mLabel.visibility = visibility
        if (visibility == View.VISIBLE) {
            mProgressBar.visibility = View.GONE
        } else {
            mProgressBar.visibility = View.VISIBLE
        }
    }

    private fun validateUserID() {

    }

    private fun showErrorDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_invalid_user, null)
        val errorDialog = android.app.AlertDialog.Builder(this@LoginActivity).create()
        errorDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

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
