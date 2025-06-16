package com.example.test.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.entity.LoginEntity
import com.example.test.domain.usecase.SaveLoginDataUseCase
import com.example.test.util.HashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val saveLoginDataUseCase: SaveLoginDataUseCase
) : ViewModel() {

    fun getPhoneValidationError(phone: String): String? {
        if (phone.isBlank()) return "شماره موبایل نمی‌تونه خالی باشه"
        if (!phone.matches(Regex("^09\\d{9}$"))) return "شماره موبایل معتبر نیست"
        return null
    }

    fun getPasswordValidationError(password: String): String? {
        if (password.isBlank()) return "رمز عبور نمی‌تونه خالی باشه"
        if (password.length < 4) return "رمز عبور باید حداقل ۴ کاراکتر باشه"
        return null
    }

    fun saveLoginData(context: Context, phone: String, password: String) {
        val hashed = HashUtil.sha256(password)
        val entity = LoginEntity(phoneNumber = phone, hashedPassword = hashed)

        // ذخیره در Room
        viewModelScope.launch {
            saveLoginDataUseCase(entity)
        }

        // ذخیره در SharedPreferences برای verify بعدی
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("login_password_hash", hashed)
            .apply()
    }

    fun verifyPassword(context: Context, enteredPassword: String): Boolean {
        val savedHash = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("login_password_hash", null) ?: return false

        return HashUtil.sha256(enteredPassword) == savedHash
    }
    fun updatePassword(context: Context, newPassword: String) {
        val hashed = HashUtil.sha256(newPassword)
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("login_password_hash", hashed)
            .apply()
    }
    fun isPreviouslyLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .contains("login_password_hash")
    }


}
