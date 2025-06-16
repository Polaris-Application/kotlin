// data/repository/DNSTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.Dao.DNSTestDao
import com.example.test.data.local.entity.DNSTestEntity
import com.example.test.domain.repository.DNSTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DNSTestRepositoryImpl @Inject constructor(
    private val dnsTestDao: DNSTestDao
) : DNSTestRepository {
    override suspend fun insertDNSTest(dnsTest: DNSTestEntity) {
        dnsTestDao.insertDNSTest(dnsTest)
    }

    override fun getAllDNSTests(): Flow<List<DNSTestEntity>> {
        return dnsTestDao.getAllDNSTests()
    }

    override suspend fun clearAllDNSTests() {
        dnsTestDao.clearAllDNSTests()
    }
}
