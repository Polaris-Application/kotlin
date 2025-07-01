package com.example.test.domain.usecase

import com.example.test.domain.repository.LoginRepository
import javax.inject.Inject

class DeleteLoginDataUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllLoginData()
    }
}
