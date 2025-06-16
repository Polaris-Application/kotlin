package com.example.test.data.local.source

import kotlinx.coroutines.flow.Flow
import com.example.test.data.local.Dao.SMSTestDao
import com.example.test.data.local.entity.SMSTestEntity
import javax.inject.Inject

class SMSTestLocalDataSourceImpl @Inject constructor(
    private val dao: SMSTestDao
) : SMSTestLocalDataSource {
    override suspend fun insertSmsTest(smsTest: SMSTestEntity) {
        dao.insertSMSTest(smsTest)
    }

    override fun getAllSmsTests(): Flow<List<SMSTestEntity>> {
        return dao.getAllSMSTests()
    }

    override suspend fun clearAllSmsTests() {
        dao.clearAllSMSTests()
    }
    // پیاده‌سازی متد جدید برای دریافت رکورد بر اساس testResultId
    override suspend fun getTestById(testResultId: Long): SMSTestEntity? {
        return dao.getTestById(testResultId)
    }
}
