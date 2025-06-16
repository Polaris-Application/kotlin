package com.example.test.data.local.source


import com.example.test.data.local.Dao.*
import com.example.test.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkTestDataSourceImpl @Inject constructor(
    private val testDao: NetworkTestDao,
    private val pingTestDao: PingTestDao,
    private val dnsTestDao: DNSTestDao,
    private val smsTestDao: SMSTestDao,
    private val webTestDao: WebTestDao,
    private val httpUploadTestDao: HttpUploadTestDao,
    private val httpDownloadTestDao: HttpDownloadTestDao
) : NetworkTestDataSource {

    override suspend fun insertTest(test: NetworkTest): Long {
        return testDao.insertTest(test)
    }

    override suspend fun deleteTest(test: NetworkTest) {
        testDao.deleteTest(test)
    }

    override fun getAllTests(): Flow<List<NetworkTest>> {
        return testDao.getAllTests()
    }

    override fun getTestById(testId: Long): Flow<NetworkTest?> {
        return testDao.getTestById(testId)
    }

    // برای ذخیره هر نوع تست
    override suspend fun addPingTestResult(pingTestEntity: PingTestEntity) {
        pingTestDao.insertPingTest(pingTestEntity)
    }

    override suspend fun addDnsTestResult(dnsTestEntity: DNSTestEntity) {
        dnsTestDao.insertDNSTest(dnsTestEntity)
    }

    override suspend fun addSmsTestResult(smsTestEntity: SMSTestEntity) {
        smsTestDao.insertSMSTest(smsTestEntity)
    }

    override suspend fun addWebTestResult(webTestEntity: WebTestEntity) {
        webTestDao.insertWebTest(webTestEntity)
    }

    override suspend fun addHttpUploadTestResult(httpUploadTestEntity: HttpUploadTestEntity) {
        httpUploadTestDao.insertHttpUploadTest(httpUploadTestEntity)
    }

    override suspend fun addHttpDownloadTestResult(httpDownloadTestEntity: HttpDownloadTestEntity) {
        httpDownloadTestDao.insertHttpDownloadTest(httpDownloadTestEntity)
    }

    // برای دریافت نتایج هر تست
    override fun getPingTestResults(testId: Long): Flow<List<PingTestEntity>> {
        return pingTestDao.getResultsForTest(testId)
    }

    override fun getDnsTestResults(testId: Long): Flow<List<DNSTestEntity>> {
        return dnsTestDao.getResultsForTest(testId)
    }

    override fun getSmsTestResults(testId: Long): Flow<List<SMSTestEntity>> {
        return smsTestDao.getResultsForTest(testId)
    }

    override fun getWebTestResults(testId: Long): Flow<List<WebTestEntity>> {
        return webTestDao.getResultsForTest(testId)
    }

    override fun getHttpUploadTestResults(testId: Long): Flow<List<HttpUploadTestEntity>> {
        return httpUploadTestDao.getResultsForTest(testId)
    }

    override fun getHttpDownloadTestResults(testId: Long): Flow<List<HttpDownloadTestEntity>> {
        return httpDownloadTestDao.getResultsForTest(testId)
    }
    override suspend fun updateTest(test: NetworkTest) {
        testDao.updateTest(test)
    }
}
