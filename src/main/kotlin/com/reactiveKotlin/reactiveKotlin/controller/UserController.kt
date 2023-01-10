package com.reactiveKotlin.reactiveKotlin.controller

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.repository.UserRepository
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.request.UserUpdateRequest
import com.reactiveKotlin.reactiveKotlin.response.PagingResponse
import com.reactiveKotlin.reactiveKotlin.response.UserCreateResponse
import com.reactiveKotlin.reactiveKotlin.response.UserUpdateResponse
import com.reactiveKotlin.reactiveKotlin.service.UserService
import jakarta.validation.Valid
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrElse
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
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
class UserController {

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var userService: UserService

    @PostMapping("")
    suspend fun createUser(
        @RequestBody @Valid request: UserCreateRequest
    ): UserCreateResponse {
       val existingUser =  userRepository.findByEmail(request.email).awaitFirstOrNull()
        if(existingUser != null){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate user")
        }
        val user = userService.makeUserCreateRequest(request)
        val createdUser = try{
            userRepository.save(user).awaitFirstOrNull()
        }catch (e: Exception){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create user",e)
        }
        val id = createdUser?.id ?:
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing id for created user")
        return userService.makeUserCreateResponse(createdUser)
    }

    @GetMapping("")
    suspend fun listUsers(
        @RequestParam pageNo: Int = 1,
        @RequestParam pageSize: Int = 10
    ): PagingResponse<User> {
        var limit = pageSize
        var offset = (limit * pageNo) - limit
        val list = userRepository.findAllUsers(limit, offset).collectList().awaitFirst()
        val total = userRepository.count().awaitFirst()
        return PagingResponse(total, list)
    }

    @PatchMapping("/{userId}")
    suspend fun updateUser(
        @PathVariable userId: Int,
        @RequestBody @Valid userUpdateRequest: UserUpdateRequest
    ): UserUpdateResponse {
        var existingDBUser = userRepository.findById(userId).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #$userId doesn't exist")
        }
        val duplicateUser =  userRepository.findByEmail(userUpdateRequest.email).awaitFirstOrNull()
        if(duplicateUser != null && duplicateUser.id != userId){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate user")
        }
        val updateUser = try {
            existingDBUser = userService.makeUserUpdateRequest(userUpdateRequest, existingDBUser)
            userRepository.save(existingDBUser).awaitFirst()
        }catch (e: Exception){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update user",e)
        }
        return userService.makeUserUpdateResponse(updateUser)
    }

    @DeleteMapping("/{userId}")
    suspend fun deleteUser(
        @PathVariable userId: Int
    ) {
        val existingUser = userRepository.findById(userId).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #$userId doesn't exist")
        }
        userRepository.delete(existingUser).subscribe()
    }

}