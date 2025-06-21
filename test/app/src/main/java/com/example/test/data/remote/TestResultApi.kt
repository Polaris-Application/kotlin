package com.example.test.data.remote

import com.example.test.data.remote.dto.TestResultRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// api interface :
interface TestResultApi {
    @POST("tests/user/")
    suspend fun sendTestResult(
        @Header("Authorization") token: String,
        @Body request: TestResultRequest
    ): Response<Unit>
}
