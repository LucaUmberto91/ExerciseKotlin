package com.reactiveKotlin.reactiveKotlin.service

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.repository.UserRepository
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.request.UserUpdateRequest
import com.reactiveKotlin.reactiveKotlin.response.PagingResponse
import com.reactiveKotlin.reactiveKotlin.response.UserCreateResponse
import com.reactiveKotlin.reactiveKotlin.response.UserUpdateResponse
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrElse
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserServiceImpl(val userRepo : UserRepository) : UserService  {
    /*override suspend fun makeUserCreateRequest(request: UserCreateRequest) : User{
        val existingUser =  userRepo.findByEmail(request.email).awaitFirstOrNull()
        if(existingUser != null){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate user")
        }
        val user = User( id = null,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName)

        userRepo.save(user).awaitFirstOrNull()
        return user
    }*/

    override suspend fun makeUserCreateRequest(request: UserCreateRequest) : User =
        userRepo.findByEmail(request.email).awaitFirstOrNull()?.let {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate user")
        } ?: run {
            val user = User(
                id = null,
                email = request.email,
                firstName = request.firstName,
                lastName = request.lastName
            )
            userRepo.save(user).awaitFirstOrNull()
            return user
        }


    override fun makeUserCreateResponse(user: User): UserCreateResponse {
        return UserCreateResponse(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName
        )
    }
   /* override suspend fun makeUserUpdateRequest(request: UserUpdateRequest, idUser : Int) : User{
        val existingDBUser = userRepo.findById(idUser).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #${idUser} doesn't exist")
        }
        existingDBUser.email = request.email
        existingDBUser.firstName = request.firstName!!
        existingDBUser.lastName = request.lastName!!

        userRepo.save(existingDBUser).awaitFirst()
        return existingDBUser
    }*/

    override suspend fun makeUserUpdateRequest(request: UserUpdateRequest, idUser : Int) : User =
        userRepo.findById(idUser).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #${idUser} doesn't exist")
        }.apply {
            email = request.email
            firstName = request.firstName!!
            lastName = request.lastName!!
        }.also { userRepo.save(it).awaitFirst() }

    override fun makeUserUpdateResponse(user: User): UserUpdateResponse {
        return UserUpdateResponse(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName
        )
    }

    override suspend fun getAllUsers(pageNumber: Int, pageSize: Int): PagingResponse<User> {
        val limit = pageSize
        val offset = (limit * pageNumber) - limit
        val list = userRepo.findAllUsers(limit, offset).collectList().awaitFirst()
        val total = userRepo.count().awaitFirst()
        return PagingResponse(total, list)
    }

    override suspend fun deleteUserFromId(idUser: Int) {
        val existingUser = userRepo.findById(idUser).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #$idUser doesn't exist")
        }
        userRepo.delete(existingUser).subscribe()
    }

    override suspend fun getUserFromId(idUser: Int) : User{
        val existingUser = userRepo.findById(idUser).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #$idUser doesn't exist")
        }
        return existingUser
    }


}