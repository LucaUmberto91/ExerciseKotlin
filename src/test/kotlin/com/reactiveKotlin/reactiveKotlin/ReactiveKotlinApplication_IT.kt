package com.reactiveKotlin.reactiveKotlin

import com.reactiveKotlin.reactiveKotlin.model.User
import com.reactiveKotlin.reactiveKotlin.request.UserCreateRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
class ReactiveKotlinApplication_IT {

	@Autowired
	private lateinit var webTestClient: WebTestClient


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
		webTestClient.get().uri("/users?pageNo=2&pageSize=10").exchange()
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

	@Test
	fun getUser() {
		val existingUserId = 2
		val nonExistingUserId = 999

		// Test deleting an existing user
		webTestClient.get().uri("/users/$existingUserId").exchange()
			.expectStatus().isOk
		// Test deleting a non-existing user
		webTestClient.get().uri("/users/$nonExistingUserId").exchange()
			.expectStatus().isNotFound
	}


}
