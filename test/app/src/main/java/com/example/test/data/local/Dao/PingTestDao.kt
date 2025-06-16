package com.example.test.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.data.local.entity.PingTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PingTestDao {

    @Insert
    suspend fun insertPingTest(pingTest: PingTestEntity)

    @Query("SELECT * FROM ping_test WHERE testId = :testId")
    fun getResultsForTest(testId: Long): Flow<List<PingTestEntity>>  // تغییرات جدید

    @Query("SELECT * FROM ping_test")
    fun getAllPingTests(): Flow<List<PingTestEntity>>

    @Query("DELETE FROM ping_test")
    suspend fun clearAllPingTests()
}
