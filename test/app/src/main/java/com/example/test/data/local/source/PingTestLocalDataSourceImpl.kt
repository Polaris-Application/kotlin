package com.example.test.data.local.source

import com.example.test.data.local.Dao.PingTestDao
import com.example.test.data.local.entity.PingTestEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow


class PingTestLocalDataSourceImpl @Inject constructor(
    private val dao: PingTestDao
) : PingTestLocalDataSource {

    override suspend fun insertPingTest(pingTest: PingTestEntity) {
        dao.insertPingTest(pingTest)
    }

    override fun getAllPingTests(): Flow<List<PingTestEntity>> {
        return dao.getAllPingTests()
    }

    override suspend fun clearAllPingTests() {
        dao.clearAllPingTests()
    }
}
