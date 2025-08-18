package com.example.test.domain.usecase

import com.example.test.domain.repository.WebTestRepository
import com.example.test.data.local.entity.WebTestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertWebTestUseCase @Inject constructor(
    private val repository: WebTestRepository
) {
    suspend operator fun invoke(webTest: WebTestEntity) = repository.insertWebTest(webTest)
}

class GetAllWebTestsUseCase @Inject constructor(
    private val repository: WebTestRepository
) {
    operator fun invoke(): Flow<List<WebTestEntity>> = repository.getAllWebTests()
}

class ClearAllWebTestsUseCase @Inject constructor(
    private val repository: WebTestRepository
) {
    suspend operator fun invoke() = repository.clearAllWebTests()
}
class GetUnsentWebTestsUseCase @Inject constructor(
    private val repo: WebTestRepository
) {
    suspend operator fun invoke(): List<WebTestEntity> = repo.getUnsentWebTests()
}

class MarkWebTestsAsUploadedUseCase @Inject constructor(
    private val repo: WebTestRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repo.markWebTestsAsUploaded(ids)
}

