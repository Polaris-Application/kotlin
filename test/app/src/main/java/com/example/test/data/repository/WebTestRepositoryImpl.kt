// data/repository/WebTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.source.WebTestLocalDataSource
import com.example.test.data.local.entity.WebTestEntity
import com.example.test.domain.repository.WebTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class WebTestRepositoryImpl @Inject constructor(
    private val webTestLocalDataSource: WebTestLocalDataSource
) : WebTestRepository {
    override suspend fun insertWebTest(webTest: WebTestEntity) {
        webTestLocalDataSource.insertWebTest(webTest)
    }

    override fun getAllWebTests(): Flow<List<WebTestEntity>> {
        return webTestLocalDataSource.getAllWebTests()
    }

    override suspend fun clearAllWebTests() {
        webTestLocalDataSource.clearAllWebTests()
    }

    override suspend fun getUnsentWebTests() = webTestLocalDataSource.getUnsentWebTests()
    override suspend fun markWebTestsAsUploaded(ids: List<Long>) = webTestLocalDataSource.markWebTestsAsUploaded(ids)

}
