package com.example.test.domain.repository


import com.example.test.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface NetworkTestRepository {
    suspend fun addTest(test: NetworkTest): Long
    suspend fun removeTest(test: NetworkTest)
    fun getAllTests(): Flow<List<NetworkTest>>
    suspend fun pauseTest(id: Long)
    suspend fun resumeTest(id: Long)
    // متد ذخیره نتیجه Ping Test
    suspend fun addPingTestResult(pingTestEntity: PingTestEntity)

    // متد ذخیره نتیجه DNS Test
    suspend fun addDnsTestResult(dnsTestEntity: DNSTestEntity)

    // متد ذخیره نتیجه SMS Test
    suspend fun addSmsTestResult(smsTestEntity: SMSTestEntity)

    // متد ذخیره نتیجه Web Test
    suspend fun addWebTestResult(webTestEntity: WebTestEntity)

    // متد ذخیره نتیجه Http Upload Test
    suspend fun addHttpUploadTestResult(httpUploadTestEntity: HttpUploadTestEntity)

    // متد ذخیره نتیجه Http Download Test
    suspend fun addHttpDownloadTestResult(httpDownloadTestEntity: HttpDownloadTestEntity)

    // متد دریافت نتایج Ping Test
    fun getPingTestResults(testId: Long): Flow<List<PingTestEntity>>

    // متد دریافت نتایج DNS Test
    fun getDnsTestResults(testId: Long): Flow<List<DNSTestEntity>>

    // متد دریافت نتایج SMS Test
    fun getSmsTestResults(testId: Long): Flow<List<SMSTestEntity>>

    // متد دریافت نتایج Web Test
    fun getWebTestResults(testId: Long): Flow<List<WebTestEntity>>

    // متد دریافت نتایج Http Upload Test
    fun getHttpUploadTestResults(testId: Long): Flow<List<HttpUploadTestEntity>>

    // متد دریافت نتایج Http Download Test
    fun getHttpDownloadTestResults(testId: Long): Flow<List<HttpDownloadTestEntity>>
    fun getTestById(testId: Long): Flow<NetworkTest?>

}


