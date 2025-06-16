package com.example.test.domain.repository

import com.example.test.data.local.entity.HttpDownloadTestEntity
import kotlinx.coroutines.flow.Flow

interface HttpDownloadTestRepository {
    suspend fun insertHttpDownloadTest(httpDownloadTest: HttpDownloadTestEntity)
    fun getAllHttpDownloadTests(): Flow<List<HttpDownloadTestEntity>>
    suspend fun clearAllHttpDownloadTests()
}
