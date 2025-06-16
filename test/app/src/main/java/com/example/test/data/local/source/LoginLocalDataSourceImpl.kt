package com.example.test.data.local.source


import com.example.test.data.local.source.LoginLocalDataSource
import com.example.test.data.local.Dao.LoginDao
import com.example.test.data.local.entity.LoginEntity
import javax.inject.Inject

class LoginLocalDataSourceImpl @Inject constructor(
    private val loginDao: LoginDao
) : LoginLocalDataSource {
    override suspend fun saveLogin(loginEntity: LoginEntity) {
        loginDao.insertLoginData(loginEntity)
    }

    override suspend fun getLogin(phone: String): LoginEntity? {
        return loginDao.getLoginData(phone)
    }
}
