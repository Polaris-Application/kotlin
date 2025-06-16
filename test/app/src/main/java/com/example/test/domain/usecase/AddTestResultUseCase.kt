package com.example.test.domain.usecase

import com.example.test.data.local.entity.*
import com.example.test.domain.repository.NetworkTestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// UseCase برای ذخیره نتیجه هر نوع تست
class AddTestResultUseCase @Inject constructor(
    private val repository: NetworkTestRepository
) {
    suspend operator fun invoke(testResult: Any) {
        when (testResult) {
            is PingTestEntity -> repository.addPingTestResult(testResult)
            is DNSTestEntity -> repository.addDnsTestResult(testResult)
            is SMSTestEntity -> repository.addSmsTestResult(testResult)
            is WebTestEntity -> repository.addWebTestResult(testResult)
            is HttpUploadTestEntity -> repository.addHttpUploadTestResult(testResult)
            is HttpDownloadTestEntity -> repository.addHttpDownloadTestResult(testResult)
        }
    }
}