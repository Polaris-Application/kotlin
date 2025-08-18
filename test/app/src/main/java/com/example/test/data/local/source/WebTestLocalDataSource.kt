package com.example.test.data.local.source

import com.example.test.data.local.entity.WebTestEntity
import kotlinx.coroutines.flow.Flow

interface WebTestLocalDataSource {
    suspend fun insertWebTest(webTest: WebTestEntity)
    fun getAllWebTests(): Flow<List<WebTestEntity>>
    suspend fun clearAllWebTests()
    suspend fun getUnsentWebTests(): List<WebTestEntity>
    suspend fun markWebTestsAsUploaded(ids: List<Long>)
}
