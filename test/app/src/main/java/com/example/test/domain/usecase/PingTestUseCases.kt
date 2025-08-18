package com.example.test.domain.usecase

import com.example.test.domain.repository.PingTestRepository
import com.example.test.data.local.entity.PingTestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertPingTestUseCase @Inject constructor(
    private val repository: PingTestRepository
) {
    suspend operator fun invoke(pingTest: PingTestEntity) = repository.insertPingTest(pingTest)
}

class GetAllPingTestsUseCase @Inject constructor(
    private val repository: PingTestRepository
) {
    operator fun invoke(): Flow<List<PingTestEntity>> = repository.getAllPingTests()
}

class ClearAllPingTestsUseCase @Inject constructor(
    private val repository: PingTestRepository
) {
    suspend operator fun invoke() = repository.clearAllPingTests()
}


class GetUnsentPingTestsUseCase @Inject constructor(
    private val repository: PingTestRepository
) {
    suspend operator fun invoke(): List<PingTestEntity> = repository.getUnsentPingTests()
}

class MarkPingTestsAsUploadedUseCase @Inject constructor(
    private val repository: PingTestRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.markPingTestsAsUploaded(ids)
}
