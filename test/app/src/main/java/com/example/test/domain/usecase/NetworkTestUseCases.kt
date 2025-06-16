package com.example.test.domain.usecase


data class NetworkTestUseCases(
    val addTest: AddTestUseCase,
    val getAllTests: GetAllTestsUseCase,
    val removeTest: RemoveTestUseCase,
    val addTestResult: AddTestResultUseCase,  // متد برای ذخیره نتیجه هر تست
    val getResultsByTestId: GetResultsByTestIdUseCase, // متد برای دریافت نتایج بر اساس testId
    val getTestById: GetTestById,
    val pauseTest: PauseTest,
    val resumeTest: ResumeTest

)
