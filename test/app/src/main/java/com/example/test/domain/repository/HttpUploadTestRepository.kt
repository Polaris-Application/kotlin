package com.example.test.domain.repository

import com.example.test.data.local.entity.HttpUploadTestEntity
import kotlinx.coroutines.flow.Flow

interface HttpUploadTestRepository {
    suspend fun insertHttpUploadTest(httpUploadTest: HttpUploadTestEntity)
    fun getAllHttpUploadTests(): Flow<List<HttpUploadTestEntity>>
    suspend fun clearAllHttpUploadTests()
    suspend fun getUnsentUploadTests(): List<HttpUploadTestEntity>
    suspend fun markUploadTestsAsUploaded(ids: List<Long>)
}
