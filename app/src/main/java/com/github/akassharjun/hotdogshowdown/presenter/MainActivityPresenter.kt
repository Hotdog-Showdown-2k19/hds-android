package com.github.akassharjun.hotdogshowdown.presenter

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivityPresenter(val view: View) {

    private lateinit var database: DatabaseReference

    fun updateCollection(userID: String, roundName: String, amount: String) {
        database = FirebaseDatabase.getInstance().reference

        Log.d("API VALUES", "$userID $roundName $amount")
        AndroidNetworking.post("https://rciit.org/hdscounter/assets/fonts/randomFonts/hdsapi.php")
                .addBodyParameter("id", userID)
                .addBodyParameter(roundName, amount)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String) {
                        Log.d("API RESPONSE", response)
                    }

                    override fun onError(error: ANError) {
                        Log.d("API ERROR", error.toString())
                        view.showAPIErrorDialog()
                    }
                })

        database.child("user1").setValue(amount)

    }


    interface View {
        fun setPreviousData(previousAmount: String)
        fun showAPIErrorDialog()
    }

}