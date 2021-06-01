package com.example.slickypasscode

import com.example.slickypasscode.network.*
import com.example.slickypasscode.network.ServerInterface.*
import org.junit.Test

import org.junit.Assert.*

class MockServerUnitTests {
    @Test(expected = NetworkException.UnknownEndpoint::class)
    fun api_throwsOnBlahEndpoint() {
        SlickyMockServer.sendApiCall("blah", mapOf())
    }

    @Test(expected = NetworkException.UnknownParams::class)
    fun api_throwsOnNoUsername() {
        SlickyMockServer.sendApiCall("getPasscodeInternals", mapOf())
    }

    @Test(expected = NetworkException.UnknownParams::class)
    fun api_throwsOnUsernameNoPassword() {
        SlickyMockServer.sendApiCall(
            "getPasscodeInternals",
            mapOf(ParameterKeys.username to "a")
        )
    }

    @Test
    fun api_failsOnWrongUsernameWrongPassword() {
        assertEquals(
            SlickyMockServer.sendApiCall(
                "getPasscodeInternals", mapOf(
                    ParameterKeys.username to "a",
                    ParameterKeys.password to "a"
                )
            ).code,
            NetworkResult.FAILURE
        )
    }

    @Test
    fun api_failsOnWrongUsernameCorrectPassword() {
        assertEquals(
            SlickyMockServer.sendApiCall(
                "getPasscodeInternals", mapOf(
                    ParameterKeys.username to "a",
                    ParameterKeys.password to "Run!@0p"
                )
            ).code,
            NetworkResult.FAILURE
        )
    }

    @Test
    fun api_failsOnCorrectUsernameWrongPassword() {
        assertEquals(
            SlickyMockServer.sendApiCall(
                "getPasscodeInternals", mapOf(
                    ParameterKeys.username to "Runloop",
                    ParameterKeys.password to "Runloop"
                )
            ).code,
            NetworkResult.FAILURE
        )
    }

    @Test
    fun api_succeedsOnCorrectUsernameCorrectPassword() {
        assertEquals(
            SlickyMockServer.sendApiCall(
                "getPasscodeInternals", mapOf(
                    ParameterKeys.username to "Runloop",
                    ParameterKeys.password to "Run!@0p"
                )
            ).code,
            NetworkResult.SUCCESS
        )
    }
}