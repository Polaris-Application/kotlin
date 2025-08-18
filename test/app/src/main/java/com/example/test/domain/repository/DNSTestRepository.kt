package com.example.test.domain.repository

import com.example.test.data.local.entity.DNSTestEntity
import kotlinx.coroutines.flow.Flow

interface DNSTestRepository {
    suspend fun insertDNSTest(dnsTest: DNSTestEntity)
    fun getAllDNSTests(): Flow<List<DNSTestEntity>>
    suspend fun clearAllDNSTests()
    suspend fun getUnsentDnsTests(): List<DNSTestEntity>
    suspend fun markDnsTestsAsUploaded(ids: List<Long>)
}
