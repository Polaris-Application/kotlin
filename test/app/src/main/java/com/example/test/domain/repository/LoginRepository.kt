package com.example.test.domain.repository


import com.example.test.data.local.entity.LoginEntity

interface LoginRepository {
    suspend fun saveLoginData(loginEntity: LoginEntity)
    suspend fun getLoginData(phone: String): LoginEntity?
}
