package com.example.test.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.entity.LoginEntity
import com.example.test.data.remote.AuthApi
import com.example.test.domain.usecase.SaveLoginDataUseCase
import com.example.test.util.HashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.test.data.remote.*
import com.example.test.domain.usecase.DeleteLoginDataUseCase


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val saveLoginDataUseCase: SaveLoginDataUseCase,
    private val deleteLoginDataUseCase: DeleteLoginDataUseCase,
    private val authApi: AuthApi
) : ViewModel() {

    var loginState by mutableStateOf<Result<String>?>(null)
        private set

    fun loginUser(context: Context, phone: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authApi.login(LoginRequest(phone, password))
                if (response.isSuccessful) {
                    val tokens = response.body()
                    tokens?.let {
                        saveLoginData(context, phone, password)
                        saveTokensToPrefs(context, it.access, it.refresh)
                        loginState = Result.success("ورود موفقیت‌آمیز بود")
                    } ?: run {
                        loginState = Result.failure(Exception("توکن دریافت نشد"))
                    }
                } else {
                    //loginState = Result.success("ورود موفقیت‌آمیز بود")
                    loginState = Result.failure(Exception("ورود ناموفق: ${response.code()}"))
                }
            } catch (e: Exception) {
                //loginState = Result.success("ورود موفقیت‌آمیز بود")
                loginState = Result.failure(e)
            }
        }
    }

    fun signUpUser(context: Context, phone: String, password: String, confirmPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authApi.signUp(SignUpRequest(phone, password, confirmPassword))
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("ثبت‌نام ناموفق: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("خطا در ثبت‌نام: ${e.localizedMessage}")
            }
        }
    }

    private fun saveTokensToPrefs(context: Context, access: String, refresh: String) {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("access_token", access)
            .putString("refresh_token", refresh)
            .apply()
    }


    fun getPhoneValidationError(phone: String): String? {
        if (phone.isBlank()) return "شماره موبایل نمی‌تونه خالی باشه"
        if (!phone.matches(Regex("^09\\d{9}$"))) return "شماره موبایل معتبر نیست"
        return null
    }

    fun getPasswordValidationError(password: String): String? {
        if (password.isBlank()) return "رمز عبور نمی‌تونه خالی باشه"
        if (password.length < 8) return "رمز عبور باید حداقل 8 کاراکتر باشه"
        if (!password.any { it.isLetter() }) return "رمز عبور باید حداقل یک حرف داشته باشه"
        if (!password.any { it.isDigit() }) return "رمز عبور باید حداقل یک عدد داشته باشه"
        return null
    }

    fun saveLoginData(context: Context, phone: String, password: String) {
        val hashed = HashUtil.sha256(password)
        val entity = LoginEntity(phoneNumber = phone, hashedPassword = hashed)

        // ذخیره در Room
        viewModelScope.launch {
            saveLoginDataUseCase(entity)
        }

        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("login_password_hash", hashed)
            .putString("user_phone_number", phone)
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

    fun reLoginWithProvidedPassword(context: Context, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val phoneNumber = prefs.getString("user_phone_number", null)

                if (phoneNumber == null) {
                    onError("شماره موبایل یافت نشد. لطفاً دوباره وارد شوید.")
                    return@launch
                }

                val response = authApi.login(LoginRequest(phoneNumber, password))
                if (response.isSuccessful) {
                    val tokens = response.body()
                    tokens?.let {
                        saveTokensToPrefs(context, it.access, it.refresh)
                        onSuccess()
                    } ?: run {
                        onError("توکن جدید دریافت نشد.")
                    }
                } else {
                    onError("خطا در دریافت توکن جدید: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("خطا در ارتباط با سرور: ${e.localizedMessage}")
            }
        }
    }
    fun logout(context: Context) {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        viewModelScope.launch {
            deleteLoginDataUseCase() // ← حذف از Room
        }
    }





}







