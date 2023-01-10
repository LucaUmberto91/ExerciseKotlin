package com.reactiveKotlin.reactiveKotlin.request

import jakarta.validation.constraints.Email

data class UserUpdateRequest(
    var firstName: String?,
    var lastName: String?,

    @field:Email
    var email: String
)
