package com.example.test.domain.usecase


import com.example.test.data.local.entity.*
import com.example.test.domain.repository.NetworkTestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// UseCase برای دریافت نتایج تست‌ها بر اساس testId
class GetResultsByTestIdUseCase @Inject constructor(
    private val repository: NetworkTestRepository
) {
    operator fun invoke(testId: Long, testType: String): Flow<List<Any>> {
        return when (testType) {
            "ping" -> repository.getPingTestResults(testId)
            "dns" -> repository.getDnsTestResults(testId)
            "sms" -> repository.getSmsTestResults(testId)
            "web" -> repository.getWebTestResults(testId)
            "upload" -> repository.getHttpUploadTestResults(testId)
            "download" -> repository.getHttpDownloadTestResults(testId)
            else -> throw IllegalArgumentException("Unknown test type: $testType")
        }
    }
}
