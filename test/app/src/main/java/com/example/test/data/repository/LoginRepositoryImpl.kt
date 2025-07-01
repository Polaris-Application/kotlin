package com.example.test.data.repository


import com.example.test.data.local.source.LoginLocalDataSource
import com.example.test.data.local.entity.LoginEntity
import com.example.test.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val localDataSource: LoginLocalDataSource
) : LoginRepository {
    override suspend fun saveLoginData(loginEntity: LoginEntity) {
        localDataSource.saveLogin(loginEntity)
    }

    override suspend fun getLoginData(phone: String): LoginEntity? {
        return localDataSource.getLogin(phone)
    }
    override suspend fun deleteAllLoginData() {
        localDataSource.deleteAllLoginData()
    }
}
