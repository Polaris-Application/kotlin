// data/local/dao/WebTestDao.kt
package com.example.test.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.data.local.entity.WebTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WebTestDao {
    @Insert
    suspend fun insertWebTest(webTest: WebTestEntity)

    @Query("SELECT * FROM web_test WHERE testId = :testId")
    fun getResultsForTest(testId: Long): Flow<List<WebTestEntity>>  // تغییرات جدید

    @Query("SELECT * FROM web_test")
    fun getAllWebTests(): Flow<List<WebTestEntity>>

    @Query("DELETE FROM web_test")
    suspend fun clearAllWebTests()

    @Query("SELECT * FROM web_test WHERE isuploaded = 0")
    suspend fun getUnsentWebTests(): List<WebTestEntity>

    @Query("UPDATE web_test SET isuploaded = 1 WHERE id IN (:ids)")
    suspend fun markWebTestsAsUploaded(ids: List<Long>)
}
