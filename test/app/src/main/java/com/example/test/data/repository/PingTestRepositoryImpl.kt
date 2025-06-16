// data/repository/PingTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.Dao.PingTestDao
import com.example.test.data.local.entity.PingTestEntity
import com.example.test.domain.repository.PingTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class PingTestRepositoryImpl @Inject constructor(
    private val pingTestDao: PingTestDao
) : PingTestRepository {
    override suspend fun insertPingTest(pingTest: PingTestEntity) {
        pingTestDao.insertPingTest(pingTest)
    }

    override fun getAllPingTests(): Flow<List<PingTestEntity>> {
        return pingTestDao.getAllPingTests()
    }

    override suspend fun clearAllPingTests() {
        pingTestDao.clearAllPingTests()
    }
}
