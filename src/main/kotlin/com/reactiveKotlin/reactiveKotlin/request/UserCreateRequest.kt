package com.reactiveKotlin.reactiveKotlin.request

import jakarta.validation.constraints.NotEmpty

data class UserCreateRequest(
    @field:NotEmpty
    var firstName: String,

    @field:NotEmpty
    var lastName: String,

    @field:NotEmpty
    var email: String
)
