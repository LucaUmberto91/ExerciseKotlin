package com.reactiveKotlin.reactiveKotlin.model

import jakarta.validation.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User (
    @Id
    var id: Int? = null,
    @field:NotEmpty
    var firstName: String,
    @field:NotEmpty
    var lastName: String,
    @field:NotEmpty
    var email: String
)