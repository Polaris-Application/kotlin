// data/local/dao/HttpUploadTestDao.kt
package com.example.test.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.data.local.entity.HttpUploadTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HttpUploadTestDao {
    @Insert
    suspend fun insertHttpUploadTest(httpUploadTest: HttpUploadTestEntity)

    @Query("SELECT * FROM http_upload_test WHERE testId = :testId")
    fun getResultsForTest(testId: Long): Flow<List<HttpUploadTestEntity>>  // تغییرات جدید

    @Query("SELECT * FROM http_upload_test")
    fun getAllHttpUploadTests(): Flow<List<HttpUploadTestEntity>>

    @Query("DELETE FROM http_upload_test")
    suspend fun clearAllHttpUploadTests()
}
