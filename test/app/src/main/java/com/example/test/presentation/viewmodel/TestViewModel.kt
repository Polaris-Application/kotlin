package com.example.test.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.TestSchedulerService
import com.example.test.data.local.entity.NetworkTest
import com.example.test.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val addTestUseCase: AddTestUseCase,
    private val getAllTestsUseCase: GetAllTestsUseCase,
    private val removeTestUseCase: RemoveTestUseCase,
    private val addTestResultUseCase: AddTestResultUseCase,
    private val getResultsByTestIdUseCase: GetResultsByTestIdUseCase,
    private val pauseTestUseCases: PauseTest, // <- این
    private val resumeTestUseCase: ResumeTest,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _pingTests = MutableStateFlow<List<NetworkTest>>(emptyList())
    val pingTests: StateFlow<List<NetworkTest>> = _pingTests.asStateFlow()

    private val _dnsTests = MutableStateFlow<List<NetworkTest>>(emptyList())
    val dnsTests: StateFlow<List<NetworkTest>> = _dnsTests.asStateFlow()

    private val _webTests = MutableStateFlow<List<NetworkTest>>(emptyList())
    val webTests: StateFlow<List<NetworkTest>> = _webTests.asStateFlow()

    private val _uploadTests = MutableStateFlow<List<NetworkTest>>(emptyList())
    val uploadTests: StateFlow<List<NetworkTest>> = _uploadTests.asStateFlow()

    private val _downloadTests = MutableStateFlow<List<NetworkTest>>(emptyList())
    val downloadTests: StateFlow<List<NetworkTest>> = _downloadTests.asStateFlow()

    private val _smsTests = MutableStateFlow<List<NetworkTest>>(emptyList())
    val smsTests: StateFlow<List<NetworkTest>> = _smsTests.asStateFlow()

    init {
        viewModelScope.launch {
            // جمع‌آوری همه تست‌ها و دسته‌بندی آنها به نوع تست
            getAllTestsUseCase().onEach { tests ->
                _pingTests.value = tests.filter { it.type == "ping" }
                _dnsTests.value = tests.filter { it.type == "dns" }
                _webTests.value = tests.filter { it.type == "web" }
                _uploadTests.value = tests.filter { it.type == "upload" }
                _downloadTests.value = tests.filter { it.type == "download" }
                _smsTests.value = tests.filter { it.type == "sms" }
            }.collect()
        }
    }

    // Add test to the repository
    fun addTest(test: NetworkTest, param: String) {
        viewModelScope.launch {
            when (test.type) {
                "ping" -> {
                    addTestUseCase(test)
                    Log.d("TestViewModel", "Ping test added with address: $param")
                }
                "dns" -> {
                    addTestUseCase(test)
                    Log.d("TestViewModel", "DNS test added with DNS: $param")
                }
                "web" -> {
                    addTestUseCase(test)
                    Log.d("TestViewModel", "Web test added with URL: $param")
                }
                "sms" -> {
                    addTestUseCase(test)
                    Log.d("TestViewModel", "SMS test added with phone number: $param")
                }
                "upload", "download" -> {
                    addTestUseCase(test)
                    Log.d("TestViewModel", "Upload/Download test added.")
                }
            }
            // Notify scheduler service for updates
            notifySchedulerService()
        }
    }

    // Remove test
    fun removeTest(test: NetworkTest) {
        viewModelScope.launch {
            removeTestUseCase(test)
            notifySchedulerService()
        }
    }

    // Get results for each test type
    fun getResultsForTest(testId: Long, testType: String): StateFlow<List<Any>> {
        val resultFlow = MutableStateFlow<List<Any>>(emptyList())
        viewModelScope.launch {
            when (testType) {
                "ping" -> {
                    getResultsByTestIdUseCase(testId, "ping").collect {
                        resultFlow.value = it
                    }
                }
                "dns" -> {
                    getResultsByTestIdUseCase(testId, "dns").collect {
                        resultFlow.value = it
                    }
                }
                "web" -> {
                    getResultsByTestIdUseCase(testId, "web").collect {
                        resultFlow.value = it
                    }
                }
                "sms" -> {
                    getResultsByTestIdUseCase(testId, "sms").collect {
                        resultFlow.value = it
                    }
                }
                "upload" -> {
                    getResultsByTestIdUseCase(testId, "upload").collect {
                        resultFlow.value = it
                    }
                }
                "download" -> {
                    getResultsByTestIdUseCase(testId, "download").collect {
                        resultFlow.value = it
                    }
                }
            }
        }
        return resultFlow
    }

    // Notify the scheduler service for changes
    private fun notifySchedulerService() {
        val intent = Intent("ACTION_UPDATE_TESTS")
        context.sendBroadcast(intent)
    }
    fun pauseTest(id: Long) {
        viewModelScope.launch {
            pauseTestUseCases(id)
            notifySchedulerService()
        }
    }

    fun resumeTest(id: Long) {
        viewModelScope.launch {
            resumeTestUseCase(id)
            notifySchedulerService()
        }
    }
}

