package com.example.test.data.local.source

import kotlinx.coroutines.flow.Flow
import com.example.test.data.local.Dao.DNSTestDao
import com.example.test.data.local.entity. DNSTestEntity
import javax.inject.Inject

class DNSTestLocalDataSourceImpl @Inject constructor(
    private val dao: DNSTestDao
) : DNSTestLocalDataSource {
    override suspend fun insertDNSTest(dnsTest:  DNSTestEntity) {
        dao.insertDNSTest(dnsTest)
    }

    override fun getAllDNSTests(): Flow<List< DNSTestEntity>> {
        return dao.getAllDNSTests()
    }

    override suspend fun clearAllDNSTests() {
        dao.clearAllDNSTests()
    }
    override suspend fun getUnsentDnsTests() = dao.getUnsentDnsTests()
    override suspend fun markDnsTestsAsUploaded(ids: List<Long>) = dao.markDnsTestsAsUploaded(ids)

}
