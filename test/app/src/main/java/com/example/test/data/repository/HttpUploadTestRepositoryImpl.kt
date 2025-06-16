// data/repository/HttpLoadTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.Dao.HttpUploadTestDao
import com.example.test.data.local.entity.HttpUploadTestEntity
import com.example.test.domain.repository.HttpUploadTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class HttpUploadTestRepositoryImpl @Inject constructor(
    private val httpUploadTestDao: HttpUploadTestDao
) : HttpUploadTestRepository {
    override suspend fun insertHttpUploadTest(httpUploadTest: HttpUploadTestEntity) {
        httpUploadTestDao.insertHttpUploadTest(httpUploadTest)
    }

    override fun getAllHttpUploadTests(): Flow<List<HttpUploadTestEntity>> {
        return httpUploadTestDao.getAllHttpUploadTests()
    }

    override suspend fun clearAllHttpUploadTests() {
        httpUploadTestDao.clearAllHttpUploadTests()
    }
}
