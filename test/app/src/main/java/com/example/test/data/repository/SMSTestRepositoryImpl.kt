// data/repository/SMSTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.source.SMSTestLocalDataSource
import com.example.test.data.local.entity.SMSTestEntity
import com.example.test.domain.repository.SMSTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SMSTestRepositoryImpl @Inject constructor(
    private val smsTestLocalDataSource: SMSTestLocalDataSource

) : SMSTestRepository {
    override suspend fun insertSMSTest(smsTest: SMSTestEntity) {
        smsTestLocalDataSource.insertSmsTest(smsTest)
    }

    override fun getAllSMSTests(): Flow<List<SMSTestEntity>> {
        return smsTestLocalDataSource.getAllSmsTests()
    }

    override suspend fun clearAllSMSTests() {
        smsTestLocalDataSource.clearAllSmsTests()
    }
    override suspend fun getTestById(testResultId: Long): SMSTestEntity? {
        return smsTestLocalDataSource.getTestById(testResultId)
    }

    override suspend fun getUnsentSmsTests() = smsTestLocalDataSource.getUnsentSmsTests()
    override suspend fun markSmsTestsAsUploaded(ids: List<Long>) = smsTestLocalDataSource.markSmsTestsAsUploaded(ids)
}
