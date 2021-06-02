package com.example.slickypasscode.repository

import com.example.slickypasscode.network.ServerInterface

interface MainRepository {

    suspend fun getPasscodeData(): PasscodeRequestResult

    sealed class PasscodeRequestResult {
        data class SuccessRequest(val password: String, val digits: ArrayList<Int>): PasscodeRequestResult()
        object FailureRequest: PasscodeRequestResult()
    }

    class MainRepositoryImpl constructor(
        private val serverInterface: ServerInterface,
        private val endpoint: String
    ) : MainRepository {

        override suspend fun getPasscodeData(): PasscodeRequestResult {
            val (login, password) = "Runloop" to "Run!@0p"
            val result = serverInterface.sendApiCall(
                endpoint,
                mapOf(
                    ServerInterface.ParameterKeys.username to login,
                    ServerInterface.ParameterKeys.password to password
                )
            )
            return when(result.code) {
                ServerInterface.NetworkResult.SUCCESS -> {
                    val data = result.value ?: throw IllegalArgumentException("Cannot receive password data")
                    PasscodeRequestResult.SuccessRequest(
                        data[ServerInterface.ResultKeys.CODE] as String,
                        (data[ServerInterface.ResultKeys.SETUP] as ArrayList<*>).map { it as Int } as ArrayList<Int>
                    )
                }
                ServerInterface.NetworkResult.FAILURE -> PasscodeRequestResult.FailureRequest
            }
        }
    }
}