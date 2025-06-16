package com.example.test.data.local.source

import kotlinx.coroutines.flow.Flow
import com.example.test.data.local.Dao.WebTestDao
import com.example.test.data.local.entity.WebTestEntity
import javax.inject.Inject

class WebTestLocalDataSourceImpl @Inject constructor(
    private val dao: WebTestDao
) : WebTestLocalDataSource {
    override suspend fun insertWebTest(webTest: WebTestEntity) {
        dao.insertWebTest(webTest)
    }

    override fun getAllWebTests(): Flow<List<WebTestEntity>> {
        return dao.getAllWebTests()
    }

    override suspend fun clearAllWebTests() {
        dao.clearAllWebTests()
    }
}
