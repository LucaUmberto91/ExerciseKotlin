package com.reactiveKotlin.reactiveKotlin.response

data class PagingResponse<T>(
    val total: Long,
    val items: List<T>
)
