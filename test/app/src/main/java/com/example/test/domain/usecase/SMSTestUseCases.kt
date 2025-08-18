package com.example.test.domain.usecase

import com.example.test.domain.repository.SMSTestRepository
import com.example.test.data.local.entity.SMSTestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertSMSTestUseCase @Inject constructor(
    private val repository: SMSTestRepository
) {
    suspend operator fun invoke(smsTest: SMSTestEntity) = repository.insertSMSTest(smsTest)
}

class GetAllSMSTestsUseCase @Inject constructor(
    private val repository: SMSTestRepository
) {
    operator fun invoke(): Flow<List<SMSTestEntity>> = repository.getAllSMSTests()
}

class ClearAllSMSTestsUseCase @Inject constructor(
    private val repository: SMSTestRepository
) {
    suspend operator fun invoke() = repository.clearAllSMSTests()
}
class GetTestByIdUseCase @Inject constructor(
    private val repository: SMSTestRepository
) {
    suspend operator fun invoke(testResultId: Long): SMSTestEntity? = repository.getTestById(testResultId)
    }

class GetUnsentSmsTestsUseCase @Inject constructor(
    private val repository: SMSTestRepository
) {
    suspend operator fun invoke(): List<SMSTestEntity> = repository.getUnsentSmsTests()
}

class MarkSmsTestsAsUploadedUseCase @Inject constructor(
    private val repository: SMSTestRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.markSmsTestsAsUploaded(ids)
}
