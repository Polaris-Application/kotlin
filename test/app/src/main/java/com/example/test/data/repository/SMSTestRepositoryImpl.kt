// data/repository/SMSTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.Dao.SMSTestDao
import com.example.test.data.local.entity.SMSTestEntity
import com.example.test.domain.repository.SMSTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SMSTestRepositoryImpl @Inject constructor(
    private val smsTestDao: SMSTestDao
) : SMSTestRepository {
    override suspend fun insertSMSTest(smsTest: SMSTestEntity) {
        smsTestDao.insertSMSTest(smsTest)
    }

    override fun getAllSMSTests(): Flow<List<SMSTestEntity>> {
        return smsTestDao.getAllSMSTests()
    }

    override suspend fun clearAllSMSTests() {
        smsTestDao.clearAllSMSTests()
    }
    override suspend fun getTestById(testResultId: Long): SMSTestEntity? {
        return smsTestDao.getTestById(testResultId)
    }
}
