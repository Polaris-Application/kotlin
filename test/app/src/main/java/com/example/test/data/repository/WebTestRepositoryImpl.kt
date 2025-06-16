// data/repository/WebTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.Dao.WebTestDao
import com.example.test.data.local.entity.WebTestEntity
import com.example.test.domain.repository.WebTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class WebTestRepositoryImpl @Inject constructor(
    private val webTestDao: WebTestDao
) : WebTestRepository {
    override suspend fun insertWebTest(webTest: WebTestEntity) {
        webTestDao.insertWebTest(webTest)
    }

    override fun getAllWebTests(): Flow<List<WebTestEntity>> {
        return webTestDao.getAllWebTests()
    }

    override suspend fun clearAllWebTests() {
        webTestDao.clearAllWebTests()
    }
}
