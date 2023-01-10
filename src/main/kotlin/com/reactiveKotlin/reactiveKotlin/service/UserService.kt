package com.reactiveKotlin.reactiveKotlin.service

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.request.UserUpdateRequest
import com.reactiveKotlin.reactiveKotlin.response.UserCreateResponse
import com.reactiveKotlin.reactiveKotlin.response.UserUpdateResponse
import org.springframework.stereotype.Service
import java.io.Serial


interface UserService {
    fun makeUserCreateRequest(userRequest : UserCreateRequest) : User
    fun makeUserCreateResponse(user: User) : UserCreateResponse
    fun makeUserUpdateRequest(userRequest : UserUpdateRequest, userInput : User) : User
    fun makeUserUpdateResponse(user: User) : UserUpdateResponse
}