package com.dhruva.userauthentication.utils

import com.dhruva.userauthentication.data.AuthResponse
import com.dhruva.userauthentication.data.LoginBody
import com.dhruva.userauthentication.data.RegisterBody
import com.dhruva.userauthentication.data.UniqueEmailValidationResponse
import com.dhruva.userauthentication.data.ValidateEmailBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {
    @POST("users/validate-unique-email")
    suspend fun validateEmailAddress(@Body body: ValidateEmailBody): Response<UniqueEmailValidationResponse>

    @POST("users/register")
    suspend fun registerUser(@Body body: RegisterBody): Response<AuthResponse>

    @POST("users/login")
    suspend fun loginUser(@Body body: LoginBody): Response<AuthResponse>
}