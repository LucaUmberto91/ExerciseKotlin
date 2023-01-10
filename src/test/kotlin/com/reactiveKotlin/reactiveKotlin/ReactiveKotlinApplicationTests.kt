package com.reactiveKotlin.reactiveKotlin

import com.reactiveKotlin.reactiveKotlin.controller.UserController
import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.repository.UserRepository
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import com.reactiveKotlin.reactiveKotlin.response.UserCreateResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.body
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.server.ResponseStatusException
import reactor.kotlin.core.publisher.toMono

@SpringBootTest
@AutoConfigureWebTestClient
class ReactiveKotlinApplicationTests {

	@Autowired
	private lateinit var webTestClient: WebTestClient
	@Autowired
	lateinit var userRepository: UserRepository
	@Autowired
	lateinit var userController : UserController


	@Test
	fun createUser() {
		val existingUser =  UserCreateRequest("John03","Smith03","jsmith03@outlook.com")
		val wrongUser = UserCreateRequest("Joh","","")
		val newUser = UserCreateRequest("Mario","Rossi","pippo@outlook.com")

		// Test create a duplicate user
		webTestClient.post().uri("/users").bodyValue(existingUser).exchange()
			.expectStatus().isBadRequest
		// Test create a wrong entry user
		webTestClient.post().uri("/users").bodyValue(wrongUser).exchange()
			.expectStatus().isBadRequest
		//Test create user correctly
		webTestClient.post().uri("/users").bodyValue(newUser).exchange()
			.expectStatus().isOk
	}

	@Test
	fun createAndVerifyUser() = runBlocking{
		var newUser = UserCreateRequest("Pinco","Pallino","pincoP@outlook.com")
		var createdUser = userController.createUser(newUser)
		assertEquals(newUser.email,createdUser.email)
		assertEquals(newUser.lastName,createdUser.lastName)
	}


	@Test
	fun deleteUser() {
		val existingUserId = 2
		val nonExistingUserId = 999

		// Test deleting an existing user
		webTestClient.delete().uri("/users/$existingUserId").exchange()
			.expectStatus().isOk
		// Test deleting a non-existing user
		webTestClient.delete().uri("/users/$nonExistingUserId").exchange()
			.expectStatus().isNotFound
	}

	@Test
	 fun listUsersPagination() {
		webTestClient.get().uri("/users?pageNo=1&pageSize=10").exchange()
			.expectStatus().isOk
	}

	@Test
	fun updateUser() {
		val existingUserId = 5
		val notExistingUserId = 999
		val newUser = User(2000,"Mario","Rossi","mimmo@outlook.com")

		// Test updating an existing user
		webTestClient.patch().uri("/users/$existingUserId").bodyValue(newUser).exchange()
			.expectStatus().isOk
		// Test updating a non-existing user
		webTestClient.patch().uri("/users/$notExistingUserId").bodyValue(newUser).exchange()
			.expectStatus().isNotFound
	}


}
