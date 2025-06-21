package com.example.test.data.remote

// AuthApi.kt

import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(val phone_number: String, val password: String)
data class SignUpRequest(val phone_number: String, val password1: String, val password2: String)
data class TokenResponse(val access: String, val refresh: String)

interface AuthApi {
    @POST("authentication/login/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("authentication/signup/")
    suspend fun signUp(@Body request: SignUpRequest): Response<Unit>
}
