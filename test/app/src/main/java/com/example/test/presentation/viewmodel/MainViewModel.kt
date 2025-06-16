package com.example.test.presentation.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.test.data.local.SimPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val simPrefs: SimPreferencesHelper
) : ViewModel() {

    var selectedSim = mutableStateOf(simPrefs.getSelectedSim())
        private set

    fun selectSim(sim: String) {
        selectedSim.value = sim
        simPrefs.setSelectedSim(sim)
    }

    fun clearSim() {
        simPrefs.clearSim()
        selectedSim.value = "SIM1"
    }
}
