package com.example.test.data.local.source

import com.example.test.data.local.entity.DNSTestEntity
import kotlinx.coroutines.flow.Flow

interface DNSTestLocalDataSource {
    suspend fun insertDNSTest(dnsTest: DNSTestEntity)
    fun getAllDNSTests(): Flow<List< DNSTestEntity>>
    suspend fun clearAllDNSTests()
}
