package com.reactiveKotlin.reactiveKotlin.service

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.request.UserUpdateRequest
import com.reactiveKotlin.reactiveKotlin.response.UserCreateResponse
import com.reactiveKotlin.reactiveKotlin.response.UserUpdateResponse
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService  {
    override fun makeUserCreateRequest(request: UserCreateRequest) : User{
        return User(
            id = null,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName
        )
    }

    override fun makeUserCreateResponse(user: User): UserCreateResponse {
        return UserCreateResponse(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName
        )
    }
    override fun makeUserUpdateRequest(request: UserUpdateRequest, userInput : User) : User{
        return User(
            id = null,
            email = request.email,
            firstName = request.firstName!! ,
            lastName = request.lastName!!
        )
    }

    override fun makeUserUpdateResponse(user: User): UserUpdateResponse {
        return UserUpdateResponse(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName
        )
    }
}