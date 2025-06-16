// data/local/dao/SMSTestDao.kt
package com.example.test.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.test.data.local.entity.SMSTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SMSTestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSMSTest(smsTest: SMSTestEntity)

    @Query("SELECT * FROM sms_test WHERE testId = :testId")
    fun getResultsForTest(testId: Long): Flow<List<SMSTestEntity>>  // تغییرات جدید

    @Query("SELECT * FROM sms_test WHERE id = :testResultId LIMIT 1")
    fun getTestById(testResultId: Long): SMSTestEntity?

    @Update
    suspend fun updateTestResult(smsTestEntity: SMSTestEntity)

    @Query("SELECT * FROM sms_test")
    fun getAllSMSTests(): Flow<List<SMSTestEntity>>

    @Query("DELETE FROM sms_test")
    suspend fun clearAllSMSTests()
}
