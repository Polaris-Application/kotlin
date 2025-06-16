package com.example.test.domain.usecase

import com.example.test.domain.repository.HttpDownloadTestRepository
import com.example.test.data.local.entity.HttpDownloadTestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertHttpDownloadTestUseCase @Inject constructor(
    private val repository: HttpDownloadTestRepository
) {
    suspend operator fun invoke(httpDownloadTest: HttpDownloadTestEntity) = repository.insertHttpDownloadTest(httpDownloadTest)
}

class GetAllHttpDownloadTestsUseCase @Inject constructor(
    private val repository: HttpDownloadTestRepository
) {
    operator fun invoke(): Flow<List<HttpDownloadTestEntity>> = repository.getAllHttpDownloadTests()
}

class ClearAllHttpDownloadTestsUseCase @Inject constructor(
    private val repository: HttpDownloadTestRepository
) {
    suspend operator fun invoke() = repository.clearAllHttpDownloadTests()
}
