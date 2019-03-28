package com.github.akassharjun.hotdogshowdown.model

data class User(
        var id: String = "",
        var firstName: String = "",
        var lastName: String? = "",
        var email: String = "",
        var phoneNumber: String = "",
        var gender: String = "",
        var date: Long = 0
)