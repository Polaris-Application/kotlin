package com.example.test.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.SimPreferencesHelper
import com.example.test.data.remote.MobileDataApi
import com.example.test.domain.usecase.*
import com.example.test.utility.UploadSettingsHelper
import com.example.test.utility.UploadPolicy
import com.example.test.utility.TestUploader
import com.example.test.utility.sendUnsentCellDataNow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val simPrefs: SimPreferencesHelper,
    val uploadSettings: UploadSettingsHelper,
    private val mobileDataApi: MobileDataApi,
    private val uploader: TestUploader,
    private val useCases: CellInfoUseCases,

    private val getUnsentPingTests: GetUnsentPingTestsUseCase,
    private val markPingTestsAsUploaded: MarkPingTestsAsUploadedUseCase,
    private val getUnsentDnsTests: GetUnsentDnsTestsUseCase,
    private val markDnsTestsAsUploaded: MarkDnsTestsAsUploadedUseCase,
    private val getUnsentWebTests: GetUnsentWebTestsUseCase,
    private val markWebTestsAsUploaded: MarkWebTestsAsUploadedUseCase,
    private val getUnsentUploadTests: GetUnsentUploadTestsUseCase,
    private val markUploadTestsAsUploaded: MarkUploadTestsAsUploadedUseCase,
    private val getUnsentDownloadTests: GetUnsentDownloadTestsUseCase,
    private val markDownloadTestsAsUploaded: MarkDownloadTestsAsUploadedUseCase,
    private val getUnsentSmsTests: GetUnsentSmsTestsUseCase,
    private val markSmsTestsAsUploaded: MarkSmsTestsAsUploadedUseCase
) : ViewModel() {

    var selectedSim = mutableStateOf(simPrefs.getSelectedSim())
        private set

    var cellUploadPolicy = mutableStateOf(uploadSettings.getCellUploadPolicy())
        private set

    var cellUploadInterval = mutableStateOf(uploadSettings.getCellInterval())
        private set

    var testUploadPolicy = mutableStateOf(uploadSettings.getTestUploadPolicy())
        private set

    var testUploadInterval = mutableStateOf(uploadSettings.getTestInterval())
        private set

    fun selectSim(sim: String) {
        selectedSim.value = sim
        simPrefs.setSelectedSim(sim)
    }

    fun clearSim() {
        simPrefs.clearSim()
        selectedSim.value = "SIM1"
    }

    fun setCellUploadPolicy(policy: UploadPolicy) {
        cellUploadPolicy.value = policy
        uploadSettings.setCellUploadPolicy(policy)
    }

    fun setCellUploadInterval(mins: Int) {
        cellUploadInterval.value = mins
        uploadSettings.setCellInterval(mins)
    }

    fun setTestUploadPolicy(policy: UploadPolicy) {
        testUploadPolicy.value = policy
        uploadSettings.setTestUploadPolicy(policy)
    }

    fun setTestUploadInterval(mins: Int) {
        testUploadInterval.value = mins
        uploadSettings.setTestInterval(mins)
    }

    fun sendAllUnsentTestResults(context: Context) {
        viewModelScope.launch {
            uploader.sendUnsentTestResultsNow(
                context = context,
                uploader = uploader,
                getUnsentPingTests = getUnsentPingTests,
                markPingTestsAsUploaded = markPingTestsAsUploaded,
                getUnsentDnsTests = getUnsentDnsTests,
                markDnsTestsAsUploaded = markDnsTestsAsUploaded,
                getUnsentWebTests = getUnsentWebTests,
                markWebTestsAsUploaded = markWebTestsAsUploaded,
                getUnsentUploadTests = getUnsentUploadTests,
                markUploadTestsAsUploaded = markUploadTestsAsUploaded,
                getUnsentDownloadTests = getUnsentDownloadTests,
                markDownloadTestsAsUploaded = markDownloadTestsAsUploaded,
                getUnsentSmsTests = getUnsentSmsTests,
                markSmsTestsAsUploaded = markSmsTestsAsUploaded,
                uploadSettings = uploadSettings
            )
        }
    }
    fun SendCellDataNow(context: Context) {
        viewModelScope.launch {
            sendUnsentCellDataNow(
                context,
                useCases.getUnsentCellInfo,
                useCases.markCellInfoAsUploaded,
                mobileDataApi,
                uploadSettings
            )
        }
    }
}
