package com.example.slickypasscode.network

import java.lang.Exception

interface ServerInterface {
    object ParameterKeys {
        const val username = "username"
        const val password = "password"
    }

    object ResultKeys {
        const val SETUP = "setup"
        const val CODE = "code"
    }

    sealed class NetworkException : Exception() {
        object UnknownEndpoint : NetworkException()
        object UnknownParams : NetworkException()
    }

    enum class NetworkResult {
        SUCCESS,
        FAILURE
    }

    data class Result(val code: NetworkResult, val value: Map<String, Any>? = null)

    fun sendApiCall(endpoint: String, params: Map<String, String>): Result
}