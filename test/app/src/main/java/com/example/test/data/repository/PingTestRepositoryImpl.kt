// data/repository/PingTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.source.PingTestLocalDataSource
import com.example.test.data.local.entity.PingTestEntity
import com.example.test.data.local.source.PingTestLocalDataSourceImpl
import com.example.test.domain.repository.PingTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PingTestRepositoryImpl @Inject constructor(
    private val localDataSource: PingTestLocalDataSource
) : PingTestRepository {
    override suspend fun insertPingTest(pingTest: PingTestEntity) {
        localDataSource.insertPingTest(pingTest)
    }

    override fun getAllPingTests(): Flow<List<PingTestEntity>> {
        return localDataSource.getAllPingTests()
    }

    override suspend fun clearAllPingTests() {
        localDataSource.clearAllPingTests()
    }
    override suspend fun getUnsentPingTests() = localDataSource.getUnsentPingTests()
    override suspend fun markPingTestsAsUploaded(ids: List<Long>) = localDataSource.markPingTestsAsUploaded(ids)

}
