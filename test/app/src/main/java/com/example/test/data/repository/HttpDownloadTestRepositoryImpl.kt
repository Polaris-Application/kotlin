// data/repository/HttpLoadTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.Dao.HttpDownloadTestDao
import com.example.test.data.local.entity.HttpDownloadTestEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.example.test.domain.repository.HttpDownloadTestRepository


class HttpDownloadTestRepositoryImpl @Inject constructor(
    private val httpDownloadTestDao: HttpDownloadTestDao
) : HttpDownloadTestRepository {
    override suspend fun insertHttpDownloadTest(httpDownloadTest: HttpDownloadTestEntity) {
        httpDownloadTestDao.insertHttpDownloadTest(httpDownloadTest)
    }

    override fun getAllHttpDownloadTests(): Flow<List<HttpDownloadTestEntity>> {
        return httpDownloadTestDao.getAllHttpDownloadTests()
    }

    override suspend fun clearAllHttpDownloadTests() {
        httpDownloadTestDao.clearAllHttpDownloadTests()
    }
}
