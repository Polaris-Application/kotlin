// data/repository/HttpLoadTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.source.HttpUploadTestLocalDataSource
import com.example.test.data.local.entity.HttpUploadTestEntity
import com.example.test.domain.repository.HttpUploadTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class HttpUploadTestRepositoryImpl @Inject constructor(
    private val uploadTestLocalDataSource: HttpUploadTestLocalDataSource
) : HttpUploadTestRepository {
    override suspend fun insertHttpUploadTest(httpUploadTest: HttpUploadTestEntity) {
        uploadTestLocalDataSource.insertHttpUploadTest(httpUploadTest)
    }

    override fun getAllHttpUploadTests(): Flow<List<HttpUploadTestEntity>> {
        return uploadTestLocalDataSource.getAllHttpUploadTests()
    }

    override suspend fun clearAllHttpUploadTests() {
        uploadTestLocalDataSource.clearAllHttpUploadTests()
    }

    override suspend fun getUnsentUploadTests() = uploadTestLocalDataSource.getUnsentUploadTests()
    override suspend fun markUploadTestsAsUploaded(ids: List<Long>) = uploadTestLocalDataSource.markUploadTestsAsUploaded(ids)

}
