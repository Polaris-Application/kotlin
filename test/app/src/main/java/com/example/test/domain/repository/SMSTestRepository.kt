package com.example.test.domain.repository

import com.example.test.data.local.entity.SMSTestEntity
import kotlinx.coroutines.flow.Flow

interface SMSTestRepository {
    suspend fun insertSMSTest(smsTest: SMSTestEntity)
    fun getAllSMSTests(): Flow<List<SMSTestEntity>>
    suspend fun clearAllSMSTests()
    suspend fun getTestById(testResultId: Long): SMSTestEntity?  // اضافه کردن متد جدید
    suspend fun getUnsentSmsTests(): List<SMSTestEntity>
    suspend fun markSmsTestsAsUploaded(ids: List<Long>)
}
