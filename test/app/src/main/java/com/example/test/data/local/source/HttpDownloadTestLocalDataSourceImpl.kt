package com.example.test.data.local.source

import kotlinx.coroutines.flow.Flow
import com.example.test.data.local.Dao.HttpDownloadTestDao
import com.example.test.data.local.entity.HttpDownloadTestEntity
import javax.inject.Inject

class HttpDownloadTestLocalDataSourceImpl @Inject constructor(
    private val dao: HttpDownloadTestDao
) : HttpDownloadTestLocalDataSource {
    override suspend fun insertHttpDownloadTest(httpDownloadTest: HttpDownloadTestEntity) {
        dao.insertHttpDownloadTest(httpDownloadTest)
    }

    override fun getAllHttpDownloadTests(): Flow<List<HttpDownloadTestEntity>> {
        return dao.getAllHttpDownloadTests()
    }

    override suspend fun clearAllHttpDownloadTests() {
        dao.clearAllHttpDownloadTests()
    }
}
