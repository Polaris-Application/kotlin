package com.example.test.domain.usecase


import com.example.test.data.local.entity.NetworkTest
import com.example.test.domain.repository.NetworkTestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddTestUseCase @Inject constructor(
    private val repository: NetworkTestRepository
) {
    suspend operator fun invoke(test: NetworkTest) = repository.addTest(test)
}

class GetAllTestsUseCase @Inject constructor(
    private val repository: NetworkTestRepository
) {
    operator fun invoke(): Flow<List<NetworkTest>> = repository.getAllTests()
}

class RemoveTestUseCase @Inject constructor(
    private val repository: NetworkTestRepository
) {
    suspend operator fun invoke(test: NetworkTest) = repository.removeTest(test)
}

class GetTestById @Inject constructor(
    private val networkTestRepository: NetworkTestRepository
) {
    operator fun invoke(testId: Long): Flow<NetworkTest?> {
        return networkTestRepository.getTestById(testId)  // استفاده از repository برای دریافت تست بر اساس id
    }
}

class PauseTest @Inject constructor(
    private val networkTestRepository: NetworkTestRepository
) {
    suspend operator fun invoke(id: Long) {
        networkTestRepository.pauseTest(id)
    }
}

class ResumeTest @Inject constructor(
    private val networkTestRepository: NetworkTestRepository
) {
    suspend operator fun invoke(id: Long) {
        networkTestRepository.resumeTest(id)
    }
}



