package com.example.test.domain.usecase

import com.example.test.domain.repository.DNSTestRepository
import com.example.test.data.local.entity.DNSTestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertDNSTestUseCase @Inject constructor(
    private val repository: DNSTestRepository
) {
    suspend operator fun invoke(dnsTest: DNSTestEntity) = repository.insertDNSTest(dnsTest)
}

class GetAllDNSTestsUseCase @Inject constructor(
    private val repository: DNSTestRepository
) {
    operator fun invoke(): Flow<List<DNSTestEntity>> = repository.getAllDNSTests()
}

class ClearAllDNSTestsUseCase @Inject constructor(
    private val repository: DNSTestRepository
) {
    suspend operator fun invoke() = repository.clearAllDNSTests()
}
