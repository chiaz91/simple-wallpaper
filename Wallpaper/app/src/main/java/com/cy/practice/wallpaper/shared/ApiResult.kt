package com.cy.practice.wallpaper.shared

sealed class ApiResult<out T, out E> {
    data class Success<T>(val data: T) : ApiResult<T, Nothing>()
    data class Error<E>(val error: E) : ApiResult<Nothing, E>()
}