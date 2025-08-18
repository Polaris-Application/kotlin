package com.example.test.data.local.source

import com.example.test.data.local.entity.SMSTestEntity
import kotlinx.coroutines.flow.Flow

interface SMSTestLocalDataSource {
    suspend fun insertSmsTest(smsTest: SMSTestEntity)
    fun getAllSmsTests(): Flow<List<SMSTestEntity>>
    suspend fun clearAllSmsTests()
    // متد جدید برای دریافت رکورد بر اساس testResultId
    suspend fun getTestById(testResultId: Long): SMSTestEntity?
    suspend fun getUnsentSmsTests(): List<SMSTestEntity>
    suspend fun markSmsTestsAsUploaded(ids: List<Long>)
}
