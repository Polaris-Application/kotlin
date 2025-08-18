package com.example.test.domain.usecase

import com.example.test.domain.repository.HttpUploadTestRepository
import com.example.test.data.local.entity.HttpUploadTestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertHttpUploadTestUseCase @Inject constructor(
    private val repository: HttpUploadTestRepository
) {
    suspend operator fun invoke(httpUploadTest: HttpUploadTestEntity) = repository.insertHttpUploadTest(httpUploadTest)
}

class GetAllHttpUploadTestsUseCase @Inject constructor(
    private val repository: HttpUploadTestRepository
) {
    operator fun invoke(): Flow<List<HttpUploadTestEntity>> = repository.getAllHttpUploadTests()
}

class ClearAllHttpUploadTestsUseCase @Inject constructor(
    private val repository: HttpUploadTestRepository
) {
    suspend operator fun invoke() = repository.clearAllHttpUploadTests()
}

class GetUnsentUploadTestsUseCase @Inject constructor(
    private val repository: HttpUploadTestRepository
) {
    suspend operator fun invoke(): List<HttpUploadTestEntity> = repository.getUnsentUploadTests()
}

class MarkUploadTestsAsUploadedUseCase @Inject constructor(
    private val repository: HttpUploadTestRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.markUploadTestsAsUploaded(ids)
}

