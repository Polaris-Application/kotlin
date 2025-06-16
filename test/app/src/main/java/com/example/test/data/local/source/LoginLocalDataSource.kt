package com.example.test.data.local.source


import com.example.test.data.local.entity.LoginEntity

interface LoginLocalDataSource {
    suspend fun saveLogin(loginEntity: LoginEntity)
    suspend fun getLogin(phone: String): LoginEntity?
}
