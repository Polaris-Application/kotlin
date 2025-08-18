package com.example.test.data.local.source

import com.example.test.data.local.entity.HttpDownloadTestEntity
import kotlinx.coroutines.flow.Flow

interface HttpDownloadTestLocalDataSource {
    suspend fun insertHttpDownloadTest(httpDownloadTest: HttpDownloadTestEntity)
    fun getAllHttpDownloadTests(): Flow<List<HttpDownloadTestEntity>>
    suspend fun clearAllHttpDownloadTests()
    suspend fun getUnsentDownloadTests(): List<HttpDownloadTestEntity>
    suspend fun markDownloadTestsAsUploaded(ids: List<Long>)
}
