package com.example.test.data.local.source

import com.example.test.data.local.entity.PingTestEntity
import kotlinx.coroutines.flow.Flow

interface PingTestLocalDataSource {
    suspend fun insertPingTest(pingTest: PingTestEntity)
    fun getAllPingTests(): Flow<List<PingTestEntity>>
    suspend fun clearAllPingTests()
}
