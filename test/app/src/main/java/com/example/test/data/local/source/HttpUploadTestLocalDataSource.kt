package com.example.test.data.local.source

import com.example.test.data.local.entity.HttpUploadTestEntity
import kotlinx.coroutines.flow.Flow

interface HttpUploadTestLocalDataSource {
    suspend fun insertHttpUploadTest(httpUploadTest: HttpUploadTestEntity)
    fun getAllHttpUploadTests(): Flow<List<HttpUploadTestEntity>>
    suspend fun clearAllHttpUploadTests()
}
