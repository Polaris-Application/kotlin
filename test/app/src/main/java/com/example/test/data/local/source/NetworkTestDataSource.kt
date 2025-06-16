package com.example.test.data.local.source

import com.example.test.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface NetworkTestDataSource {
    suspend fun insertTest(test: NetworkTest): Long
    suspend fun deleteTest(test: NetworkTest)
    fun getAllTests(): Flow<List<NetworkTest>>
    fun getTestById(testId: Long): Flow<NetworkTest?>
    suspend fun updateTest(test: NetworkTest)
    suspend fun addPingTestResult(pingTestEntity: PingTestEntity)
    suspend fun addDnsTestResult(dnsTestEntity: DNSTestEntity)
    suspend fun addSmsTestResult(smsTestEntity: SMSTestEntity)
    suspend fun addWebTestResult(webTestEntity: WebTestEntity)
    suspend fun addHttpUploadTestResult(httpUploadTestEntity: HttpUploadTestEntity)
    suspend fun addHttpDownloadTestResult(httpDownloadTestEntity: HttpDownloadTestEntity)

    // برای دریافت نتایج هر تست
    fun getPingTestResults(testId: Long): Flow<List<PingTestEntity>>
    fun getDnsTestResults(testId: Long): Flow<List<DNSTestEntity>>
    fun getSmsTestResults(testId: Long): Flow<List<SMSTestEntity>>
    fun getWebTestResults(testId: Long): Flow<List<WebTestEntity>>
    fun getHttpUploadTestResults(testId: Long): Flow<List<HttpUploadTestEntity>>
    fun getHttpDownloadTestResults(testId: Long): Flow<List<HttpDownloadTestEntity>>

}
