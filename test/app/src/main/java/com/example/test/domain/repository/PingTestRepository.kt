package com.example.test.domain.repository

import com.example.test.data.local.entity.PingTestEntity
import kotlinx.coroutines.flow.Flow

interface PingTestRepository {
    suspend fun insertPingTest(pingTest: PingTestEntity)
    fun getAllPingTests(): Flow<List<PingTestEntity>>
    suspend fun clearAllPingTests()
    suspend fun getUnsentPingTests(): List<PingTestEntity>
    suspend fun markPingTestsAsUploaded(ids: List<Long>)
}
