package com.github.akassharjun.hotdogshowdown.presenter

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject

class MainActivityPresenter(val view: View) {

    private lateinit var database: DatabaseReference
    private val apiURL = "https://rciit.org/hdscounter/assets/fonts/randomFonts/hdsapi.php"

    fun updateCollection(tableNumber: String, amount: String) {
        database = FirebaseDatabase.getInstance().reference

        val tName: String
        val num = tableNumber.takeLast(2).replace(" ", "").toInt()

        tName = if (num < 10) {
            "table_0${tableNumber.takeLast(1)}"
        } else {
            tableNumber.toLowerCase().replace(" ", "_")
        }

        database.child(tName).setValue(amount.toInt())
    }

    fun initializeRecord(userID: String) {
        AndroidNetworking.post(apiURL)
                .addBodyParameter("id", userID)
                .addBodyParameter("round_1", "0")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String) {
                        Log.d("API RESPONSE", response)
                    }

                    override fun onError(error: ANError) {
                        Log.d("API ERROR", error.toString())
                        view.showAPIErrorDialog("initialize")
                    }
                })
    }

    fun incrementHotdog(userID: String, roundName: String) {
        AndroidNetworking.post(apiURL)
                .addBodyParameter("id", userID)
                .addBodyParameter("inc$roundName", "1")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String) {
                        Log.d("API RESPONSE", response)
                    }

                    override fun onError(error: ANError) {
                        Log.d("API ERROR", error.toString())
                        view.showAPIErrorDialog("increment")
                    }
                })
    }

    fun decrementHotdog(userID: String, roundName: String) {
        AndroidNetworking.post(apiURL)
                .addBodyParameter("id", userID)
                .addBodyParameter("dec$roundName", "1")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(object : StringRequestListener {

                    override fun onResponse(response: String?) {
                        Log.d("API RESPONSE", response)
                    }

                    override fun onError(anError: ANError?) {
                        view.showAPIErrorDialog("decrement")
                    }
                })
    }

    fun getPreviousData(userID: String, roundName: String, tableNumber: String) {
        var rName = ""
        if (roundName == "FR") {
            rName = "firstR"
        } else {
            rName = "lastR"
        }

        AndroidNetworking.get(apiURL)
                .addQueryParameter("id", userID)
                .addQueryParameter(rName, "1")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        if (response != null) {
                            if (rName == "firstR") {
                                Log.d("API RESPONSE", response.toString())
                                if (response.get("round_1") != null) {
                                    view.setPreviousData(response.get("round_1").toString())
                                    updateCollection(tableNumber, response.get("round_1").toString())
                                }
                            } else {
                                if (response.get("round_2") != null) {
                                    view.setPreviousData(response.get("round_2").toString())
                                    updateCollection(tableNumber, response.get("round_2").toString())
                                }
                            }
                        }

                    }

                    override fun onError(anError: ANError?) {
                        view.showAPIErrorDialog("previous")
                    }

                })


    }


    interface View {
        fun setPreviousData(previousAmount: String)
        fun showAPIErrorDialog(type: String)
    }

}