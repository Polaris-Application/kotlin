// data/local/dao/HttpUploadTestDao.kt
package com.example.test.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.data.local.entity.HttpDownloadTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HttpDownloadTestDao {
    @Insert
    suspend fun insertHttpDownloadTest(httpDownloadTest: HttpDownloadTestEntity)

    @Query("SELECT * FROM http_download_test WHERE testId = :testId")
    fun getResultsForTest(testId: Long): Flow<List<HttpDownloadTestEntity>>  // تغییرات جدید

    @Query("SELECT * FROM http_download_test")
    fun getAllHttpDownloadTests(): Flow<List<HttpDownloadTestEntity>>

    @Query("DELETE FROM http_download_test")
    suspend fun clearAllHttpDownloadTests()

    @Query("SELECT * FROM http_download_test WHERE isuploaded = 0")
    suspend fun getUnsentDownloadTests(): List<HttpDownloadTestEntity>

    @Query("UPDATE http_download_test SET isuploaded = 1 WHERE id IN (:ids)")
    suspend fun markDownloadTestsAsUploaded(ids: List<Long>)
}
