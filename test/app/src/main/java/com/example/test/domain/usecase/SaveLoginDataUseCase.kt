package com.example.test.domain.usecase


import com.example.test.data.local.entity.LoginEntity
import com.example.test.domain.repository.LoginRepository
import javax.inject.Inject

class SaveLoginDataUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(loginEntity: LoginEntity) {
        repository.saveLoginData(loginEntity)
    }
}
