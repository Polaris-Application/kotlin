// data/repository/HttpLoadTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.source.HttpDownloadTestLocalDataSource
import com.example.test.data.local.entity.HttpDownloadTestEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.example.test.domain.repository.HttpDownloadTestRepository


class HttpDownloadTestRepositoryImpl @Inject constructor(
    private val httpDownloadTestLocalDataSource: HttpDownloadTestLocalDataSource
) : HttpDownloadTestRepository {
    override suspend fun insertHttpDownloadTest(httpDownloadTest: HttpDownloadTestEntity) {
        httpDownloadTestLocalDataSource.insertHttpDownloadTest(httpDownloadTest)
    }

    override fun getAllHttpDownloadTests(): Flow<List<HttpDownloadTestEntity>> {
        return httpDownloadTestLocalDataSource.getAllHttpDownloadTests()
    }

    override suspend fun clearAllHttpDownloadTests() {
        httpDownloadTestLocalDataSource.clearAllHttpDownloadTests()
    }

    override suspend fun getUnsentDownloadTests() = httpDownloadTestLocalDataSource.getUnsentDownloadTests()
    override suspend fun markDownloadTestsAsUploaded(ids: List<Long>) =httpDownloadTestLocalDataSource.markDownloadTestsAsUploaded(ids)


}
