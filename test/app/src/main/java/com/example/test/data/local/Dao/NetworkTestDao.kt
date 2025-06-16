package com.example.test.data.local.Dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.test.data.local.entity.*

@Dao
interface NetworkTestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: NetworkTest): Long

    @Delete
    suspend fun deleteTest(test: NetworkTest)

    @Query("SELECT * FROM network_tests")
    fun getAllTests(): Flow<List<NetworkTest>>

    @Query("SELECT * FROM network_tests WHERE id = :testId")
    fun getTestById(testId: Long): Flow<NetworkTest?>

    @Update
    suspend fun updateTest(test: NetworkTest)
}
