package com.reactiveKotlin.reactiveKotlin.controller

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.request.UserUpdateRequest
import com.reactiveKotlin.reactiveKotlin.response.PagingResponse
import com.reactiveKotlin.reactiveKotlin.response.UserCreateResponse
import com.reactiveKotlin.reactiveKotlin.response.UserUpdateResponse
import com.reactiveKotlin.reactiveKotlin.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.lang.Exception

@RestController
@RequestMapping("/users", produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(val userService: UserService) {


    @PostMapping("")
    suspend fun createUser(
        @RequestBody @Valid request: UserCreateRequest
    ): UserCreateResponse {
        val createdUser = try{
           userService.makeUserCreateRequest(request)
        }catch (e: Exception){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create user",e)
        }
        return userService.makeUserCreateResponse(createdUser)
    }

    @GetMapping("")
    suspend fun listUsers(
        @RequestParam pageNo: Int = 1,
        @RequestParam pageSize: Int = 10
    ): PagingResponse<User> {
        return userService.getAllUsers(pageNo,pageSize)
    }

    @PatchMapping("/{userId}")
    suspend fun updateUser(
        @PathVariable userId: Int,
        @RequestBody @Valid userUpdateRequest: UserUpdateRequest
    ): UserUpdateResponse {
        val updateUser = userService.makeUserUpdateRequest(userUpdateRequest,userId)
        return userService.makeUserUpdateResponse(updateUser)
    }

    @DeleteMapping("/{userId}")
    suspend fun deleteUser(
        @PathVariable userId: Int
    ) {
        userService.deleteUserFromId(userId)
    }

    @GetMapping("/{userId}")
    suspend fun getUser(
        @PathVariable userId: Int
    ) : User{
        return userService.getUserFromId(userId)
    }

}