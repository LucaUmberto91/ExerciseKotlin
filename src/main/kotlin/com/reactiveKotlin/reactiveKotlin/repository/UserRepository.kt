package com.reactiveKotlin.reactiveKotlin.repository

import com.reactiveKotlin.reactiveKotlin.model.User
import jakarta.validation.constraints.Email
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@EnableR2dbcRepositories
interface UserRepository : ReactiveCrudRepository<User, Int> {
    fun findByEmail(email: String): Mono<User>

    @Query("SELECT * FROM users limit :limit offset :offset")
    fun findAllUsers(limit: Int, offset: Int): Flux<User>
}