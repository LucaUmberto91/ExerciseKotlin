package com.reactiveKotlin.reactiveKotlin.response

data class UserCreateResponse(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val email: String
)
