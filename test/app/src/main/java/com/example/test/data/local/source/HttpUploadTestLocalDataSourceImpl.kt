package com.example.test.data.local.source

import kotlinx.coroutines.flow.Flow
import com.example.test.data.local.Dao.HttpUploadTestDao
import com.example.test.data.local.entity.HttpUploadTestEntity
import javax.inject.Inject

class HttpUploadTestLocalDataSourceImpl @Inject constructor(
    private val dao: HttpUploadTestDao
) : HttpUploadTestLocalDataSource {
    override suspend fun insertHttpUploadTest(httpUploadTest: HttpUploadTestEntity) {
        dao.insertHttpUploadTest(httpUploadTest)
    }

    override fun getAllHttpUploadTests(): Flow<List<HttpUploadTestEntity>> {
        return dao.getAllHttpUploadTests()
    }

    override suspend fun clearAllHttpUploadTests() {
        dao.clearAllHttpUploadTests()
    }
}
