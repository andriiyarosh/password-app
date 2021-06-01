package com.example.slickypasscode.network

import com.example.slickypasscode.network.ServerInterface.*

object SlickyMockServer : ServerInterface {
    private object Consts {
        val PASSCODE_DIGITS = 0..9
        val POSSIBLE_CODES = arrayOf("1234", "4321")

        const val POSSIBLE_USERNAME = "Runloop"
        const val POSSIBLE_PASSWORD = "Run!@0p"

        const val SUPPORTED_ENDPOINT = "getPasscodeInternals"
    }

    override fun sendApiCall(endpoint: String, params: Map<String, String>): Result {
        if (Consts.SUPPORTED_ENDPOINT != endpoint) {
            println("SlickyMockServer : sendApiCall - unknown endpoint")
            throw NetworkException.UnknownEndpoint
        }

        val username = params[ParameterKeys.username]
        val password = params[ParameterKeys.password]

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            println("SlickyMockServer : sendApiCall - unknown parameters sent")
            throw NetworkException.UnknownParams
        }

        return if (username == Consts.POSSIBLE_USERNAME && password == Consts.POSSIBLE_PASSWORD) {
            Result(
                NetworkResult.SUCCESS,
                result
            )
        } else {
            Result(NetworkResult.FAILURE)
        }
    }

    private val result: Map<String, Any>
        get() = mapOf(
            ResultKeys.SETUP to Consts.PASSCODE_DIGITS.shuffled(),
            ResultKeys.CODE to Consts.POSSIBLE_CODES.toMutableList().shuffled().first()
        )
}