package com.example.test.data.repository

import com.example.test.data.local.source.NetworkTestDataSource
import com.example.test.data.local.entity.*
import com.example.test.domain.repository.NetworkTestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class NetworkTestRepositoryImpl @Inject constructor(
    private val dataSource: NetworkTestDataSource
) : NetworkTestRepository {

    // ذخیره تست جدید
    override suspend fun addTest(test: NetworkTest): Long {
        return dataSource.insertTest(test)
    }

    // حذف تست
    override suspend fun removeTest(test: NetworkTest) {
        dataSource.deleteTest(test)
    }

    // دریافت تمام تست‌ها
    override fun getAllTests(): Flow<List<NetworkTest>> {
        return dataSource.getAllTests()
    }

    // ذخیره نتیجه Ping Test
    override suspend fun addPingTestResult(pingTestEntity: PingTestEntity) {
        dataSource.addPingTestResult(pingTestEntity)
    }

    // ذخیره نتیجه DNS Test
    override suspend fun addDnsTestResult(dnsTestEntity: DNSTestEntity) {
        dataSource.addDnsTestResult(dnsTestEntity)
    }

    // ذخیره نتیجه SMS Test
    override suspend fun addSmsTestResult(smsTestEntity: SMSTestEntity) {
        dataSource.addSmsTestResult(smsTestEntity)
    }

    // ذخیره نتیجه Web Test
    override suspend fun addWebTestResult(webTestEntity: WebTestEntity) {
        dataSource.addWebTestResult(webTestEntity)
    }

    // ذخیره نتیجه Http Upload Test
    override suspend fun addHttpUploadTestResult(httpUploadTestEntity: HttpUploadTestEntity) {
        dataSource.addHttpUploadTestResult(httpUploadTestEntity)
    }

    // ذخیره نتیجه Http Download Test
    override suspend fun addHttpDownloadTestResult(httpDownloadTestEntity: HttpDownloadTestEntity) {
        dataSource.addHttpDownloadTestResult(httpDownloadTestEntity)
    }

    // دریافت نتایج Ping Test
    override fun getPingTestResults(testId: Long): Flow<List<PingTestEntity>> {
        return dataSource.getPingTestResults(testId)
    }

    // دریافت نتایج DNS Test
    override fun getDnsTestResults(testId: Long): Flow<List<DNSTestEntity>> {
        return dataSource.getDnsTestResults(testId)
    }

    // دریافت نتایج SMS Test
    override fun getSmsTestResults(testId: Long): Flow<List<SMSTestEntity>> {
        return dataSource.getSmsTestResults(testId)
    }

    // دریافت نتایج Web Test
    override fun getWebTestResults(testId: Long): Flow<List<WebTestEntity>> {
        return dataSource.getWebTestResults(testId)
    }

    // دریافت نتایج Http Upload Test
    override fun getHttpUploadTestResults(testId: Long): Flow<List<HttpUploadTestEntity>> {
        return dataSource.getHttpUploadTestResults(testId)
    }

    // دریافت نتایج Http Download Test
    override fun getHttpDownloadTestResults(testId: Long): Flow<List<HttpDownloadTestEntity>> {
        return dataSource.getHttpDownloadTestResults(testId)
    }
    override fun getTestById(testId: Long): Flow<NetworkTest?> {
        return dataSource.getTestById(testId)  // فراخوانی متد getTestById از Dao
    }
    override suspend fun pauseTest(id: Long) {
        val current = dataSource.getAllTests().firstOrNull()
            ?.find { it.id == id }
        current?.let {
            dataSource.updateTest(it.copy(isPaused = true))
        }
    }
    override suspend fun resumeTest(id: Long) {
        val current = dataSource.getAllTests().firstOrNull()
            ?.find { it.id == id }
        current?.let {
            dataSource.updateTest(it.copy(isPaused = false))
        }
    }
}
