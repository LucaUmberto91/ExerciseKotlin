package com.reactiveKotlin.reactiveKotlin

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.repository.UserRepository
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.request.UserUpdateRequest
import com.reactiveKotlin.reactiveKotlin.service.UserServiceImpl
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import kotlin.test.assertFailsWith

@SpringBootTest
class UserServiceControllerTest{


    val userRepository = mockk<UserRepository>(relaxed = true)
    val userService = UserServiceImpl(userRepository)
    val user = User(1,"Pinco","Pallino","pinco.pallino@gmail.com")
    val request = UserCreateRequest("John","Doe","johndoe@gmail.com")
    val requestToUpdate = UserUpdateRequest("John2","Doe2","johndoe2@gmail.com")

    @BeforeEach
    fun setup(){
        clearMocks(userRepository)
    }


    @Test
    fun getUserFromId() = runBlocking{
        every { userRepository.findById(1) } returns user.toMono();

        val result = userService.getUserFromId(1)

        verify(exactly = 1) { userRepository.findById(1) }
        assertEquals(user,result)
    }

    @Test
    fun deleteUser() = runBlocking {
        every { userRepository.findById(1) } returns user.toMono()

        userService.deleteUserFromId(1)

        verify(exactly = 1) { userRepository.delete(user) }
    }

    @Test
    fun deleteUserWhenUserNotIsPresent(){
        every { userRepository.findById(1) } returns Mono.empty()

        runBlocking {
            assertFailsWith<ResponseStatusException> { userService.deleteUserFromId(1) }
        }

    }

    @Test
    fun getUserWhenUserNotIsPresent(){
        every { userRepository.findById(1) } returns Mono.empty()

        runBlocking {
            assertFailsWith<ResponseStatusException> { userService.getUserFromId(1) }
        }
    }

    @Test
    fun createUserIfItIsNotPresent() = runBlocking {
        every { userRepository.findByEmail(request.email) } returns Mono.empty()

        every { userRepository.save(any()) } returns
                Mono.just(User( id = 1, email = request.email, firstName = request.firstName, lastName = request.lastName))

        val newUser = userService.makeUserCreateRequest(request)

        assertEquals(request.firstName,newUser.firstName)
        assertEquals(request.lastName,newUser.lastName)
        assertEquals(request.email,newUser.email)
        verify { userRepository.save(any()) }
    }

    @Test
    fun createUserIfItIsAlreadyPresent() {
        every { userRepository.findByEmail(request.email) } returns
                Mono.just(User(id = 1,firstName = request.firstName, lastName = request.lastName, email = request.email))
    runBlocking {
        assertFailsWith<ResponseStatusException> { userService.makeUserCreateRequest(request) }
    }
        verify { userRepository.save(any()) wasNot Called }
    }

    @Test
    fun updateUserIfItIsAlreadyPresent() = runBlocking{
        every { userRepository.findById(1) } returns
                Mono.just(User(id = 1,firstName = request.firstName, lastName = request.lastName, email = request.email))
        every { userRepository.save(any()) } returns
                Mono.just(User( id = 1, email = request.email, firstName = request.firstName, lastName = request.lastName))

        val updatedUser = userService.makeUserUpdateRequest(requestToUpdate,1)
        assertEquals(requestToUpdate.firstName,updatedUser.firstName)
        assertEquals(requestToUpdate.lastName,updatedUser.lastName)
        assertEquals(requestToUpdate.email,updatedUser.email)

        verify { userRepository.save(any()) }
    }

    @Test
    fun updateUserIfItIsNotPresent(){
        every { userRepository.findById(1) } returns
               Mono.empty()

        runBlocking {
            assertFailsWith<ResponseStatusException> { userService.makeUserUpdateRequest(requestToUpdate,1) }
        }

        verify { userRepository.save(any()) wasNot Called }
    }

    @Test
    fun getAllUsersWithPaging() = runBlocking {
        val pageNumber = 2
        val pageSize = 10
        val list = listOf(
            User(id = 1, firstName = "John", lastName = "Doe", email = "johndoe@gmail.com" ),
            User(firstName = "John2", lastName = "Doe2", email = "johndoe2@gmail.com"))
        val total = 100
        every { userRepository.findAllUsers(pageSize, pageSize * (pageNumber - 1)) } returns list.toFlux()
        every { userRepository.count() } returns Mono.just(total.toLong())

        val result = userService.getAllUsers(pageNumber, pageSize)

        assertEquals(total.toLong(),result.total)
        assertEquals(list,result.items)
        verify { userRepository.findAllUsers(pageSize, pageSize * (pageNumber - 1)) }
        verify { userRepository.count() }
    }

}