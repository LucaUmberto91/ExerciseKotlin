package com.reactiveKotlin.reactiveKotlin.response

data class UserUpdateResponse(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val email: String
)
