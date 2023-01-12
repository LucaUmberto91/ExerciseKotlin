package com.reactiveKotlin.reactiveKotlin.service

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.request.UserUpdateRequest
import com.reactiveKotlin.reactiveKotlin.response.PagingResponse
import com.reactiveKotlin.reactiveKotlin.response.UserCreateResponse
import com.reactiveKotlin.reactiveKotlin.response.UserUpdateResponse


interface UserService {
    suspend fun makeUserCreateRequest(userRequest : UserCreateRequest) : User
    fun makeUserCreateResponse(user: User) : UserCreateResponse
    suspend fun makeUserUpdateRequest(userRequest : UserUpdateRequest, idUser : Int) : User
    fun makeUserUpdateResponse(user: User) : UserUpdateResponse
    suspend fun getAllUsers(pageNumber : Int, pageSize : Int): PagingResponse<User>
    suspend fun deleteUserFromId( idUser : Int)
    suspend fun getUserFromId( idUser : Int) : User
}