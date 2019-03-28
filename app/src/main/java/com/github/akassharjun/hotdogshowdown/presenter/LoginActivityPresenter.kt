package com.github.akassharjun.hotdogshowdown.presenter

import android.util.Log
import android.view.View
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject


class LoginActivityPresenter(val view: View) {

    fun retrieveUserFromDatabase(id: String) {
        Log.d("API ID", id)
        AndroidNetworking.get("https://rciit.org/hdscounter/assets/fonts/randomFonts/hdsapi.php")
                .addQueryParameter("id", id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.d("API RESPONSE", response.toString())

                        val firstName = response.getString("Fname")
                        val lastName = response.getString("Lname")

                        Log.d("API VARIABLES", String.format("%s %s".format(firstName, lastName)))

                        if (firstName != "null" && lastName != "null") {
                            view.nextActivity(id, firstName, lastName)
                        } else {
                            view.updateViews(android.view.View.VISIBLE)
                            view.showErrorDialog()
                        }
                    }

                    override fun onError(error: ANError) {
                        Log.d("API ERROR", error.toString())
                    }
                })
    }

    interface View {
        fun showErrorDialog()
        fun updateViews(visibility: Int)
        fun nextActivity(id: String, fName: String, lName: String)
    }

}

