// data/repository/DNSTestRepositoryImpl.kt
package com.example.test.data.repository

import com.example.test.data.local.source.DNSTestLocalDataSource
import com.example.test.data.local.entity.DNSTestEntity
import com.example.test.domain.repository.DNSTestRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DNSTestRepositoryImpl @Inject constructor(
    private val dnsTestLocalDataSource: DNSTestLocalDataSource
) : DNSTestRepository {
    override suspend fun insertDNSTest(dnsTest: DNSTestEntity) {
        dnsTestLocalDataSource.insertDNSTest(dnsTest)
    }

    override fun getAllDNSTests(): Flow<List<DNSTestEntity>> {
        return dnsTestLocalDataSource.getAllDNSTests()
    }

    override suspend fun clearAllDNSTests() {
        dnsTestLocalDataSource.clearAllDNSTests()
    }
    override suspend fun getUnsentDnsTests() = dnsTestLocalDataSource.getUnsentDnsTests()
    override suspend fun markDnsTestsAsUploaded(ids: List<Long>) = dnsTestLocalDataSource.markDnsTestsAsUploaded(ids)

}
