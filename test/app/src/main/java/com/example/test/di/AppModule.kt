package com.example.test.di

import android.content.Context
import androidx.room.Room
import com.example.test.data.local.Dao.*
import com.example.test.data.local.database.AppDatabase
import com.example.test.data.local.source.CellInfoLocalDataSource
import com.example.test.data.local.source.CellInfoLocalDataSourceImpl
import com.example.test.data.local.source.LoginLocalDataSource
import com.example.test.data.local.source.LoginLocalDataSourceImpl
import com.example.test.data.local.source.NetworkTestDataSource
import com.example.test.data.local.source.NetworkTestDataSourceImpl
import com.example.test.data.repository.CellInfoRepositoryImpl
import com.example.test.data.repository.NetworkTestRepositoryImpl
import com.example.test.data.repository.*
import com.example.test.domain.repository.CellInfoRepository
import com.example.test.domain.repository.NetworkTestRepository
import com.example.test.domain.repository.*
import com.example.test.domain.usecase.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    // Cell Info
    @Binds
    abstract fun bindCellInfoLocalDataSource(
        impl: CellInfoLocalDataSourceImpl
    ): CellInfoLocalDataSource

    @Binds
    abstract fun bindCellInfoRepository(
        impl: CellInfoRepositoryImpl
    ): CellInfoRepository

    // Network Test
    @Binds
    abstract fun bindNetworkTestDataSource(
        impl: NetworkTestDataSourceImpl
    ): NetworkTestDataSource

    @Binds
    abstract fun bindNetworkTestRepository(
        impl: NetworkTestRepositoryImpl
    ): NetworkTestRepository

    // SMSTest Repository
    @Binds
    abstract fun bindSMSTestRepository(
        impl: SMSTestRepositoryImpl
    ): SMSTestRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        // ====== DAOs ======
        @Provides fun provideCellInfoDao(db: AppDatabase): CellInfoDao = db.cellInfoDao()
        @Provides fun provideTestDao(db: AppDatabase): NetworkTestDao = db.networkTestDao()

        // ====== Network Test DAOs ======
        @Provides fun providePingTestDao(db: AppDatabase): PingTestDao = db.pingTestDao()
        @Provides fun provideDNSTestDao(db: AppDatabase): DNSTestDao = db.dnsTestDao()
        @Provides fun provideSMSTestDao(db: AppDatabase): SMSTestDao = db.smsTestDao()
        @Provides fun provideWebTestDao(db: AppDatabase): WebTestDao = db.webTestDao()
        @Provides fun provideHttpUploadTestDao(db: AppDatabase): HttpUploadTestDao = db.httpUploadTestDao()
        @Provides fun provideHttpDownloadTestDao(db: AppDatabase): HttpDownloadTestDao = db.httpDownloadTestDao()

        // ====== Cell Info UseCases ======
        @Provides fun provideInsertCellInfoUseCase(repo: CellInfoRepository) =
            InsertCellInfoUseCase(repo)

        @Provides fun provideGetAllCellInfoUseCase(repo: CellInfoRepository) =
            GetAllCellInfoUseCase(repo)

        @Provides fun provideClearAllCellInfoUseCase(repo: CellInfoRepository) =
            ClearAllCellInfoUseCase(repo)

        @Provides
        @Singleton
        fun provideCellInfoUseCases(
            insert: InsertCellInfoUseCase,
            getAll: GetAllCellInfoUseCase,
            clear: ClearAllCellInfoUseCase
        ): CellInfoUseCases = CellInfoUseCases(insert, getAll, clear)

        // ====== Network Test UseCases ======
        @Provides fun provideAddTestUseCase(repo: NetworkTestRepository) =
            AddTestUseCase(repo)

        @Provides fun provideAddTestResultUseCase(repo: NetworkTestRepository) =
            AddTestResultUseCase(repo)

        @Provides fun provideGetAllTestsUseCase(repo: NetworkTestRepository) =
            GetAllTestsUseCase(repo)

        @Provides fun provideGetResultsByTestIdUseCase(repo: NetworkTestRepository) =
            GetResultsByTestIdUseCase(repo)

        @Provides fun provideRemoveTestUseCase(repo: NetworkTestRepository) =
            RemoveTestUseCase(repo)
        @Provides fun provideGetTestById(repo: NetworkTestRepository) =  // اصلاح تزریق اینجا
            GetTestById(repo)

        @Provides
        @Singleton
        fun provideNetworkTestUseCases(
            addTest: AddTestUseCase,
            addResult: AddTestResultUseCase,
            getAll: GetAllTestsUseCase,
            getResults: GetResultsByTestIdUseCase,
            removeTest: RemoveTestUseCase,
            getTestById: GetTestById,
            pauseTest: PauseTest,
            resumeTest: ResumeTest
        ): NetworkTestUseCases = NetworkTestUseCases(
            addTest,
            addTestResult = addResult,
            getAllTests = getAll,
            getResultsByTestId = getResults,
            removeTest = removeTest,
            getTestById = getTestById,
            pauseTest= pauseTest,
            resumeTest= resumeTest
        )

        // ====== SMSTest UseCases ======
        @Provides
        @Singleton
        fun provideGetTestByIdUseCase(repository: SMSTestRepository): GetTestByIdUseCase {
            return GetTestByIdUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideInsertSMSTestUseCase(repository: SMSTestRepository): InsertSMSTestUseCase {
            return InsertSMSTestUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideGetAllSMSTestsUseCase(repository: SMSTestRepository): GetAllSMSTestsUseCase {
            return GetAllSMSTestsUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideClearAllSMSTestsUseCase(repository: SMSTestRepository): ClearAllSMSTestsUseCase {
            return ClearAllSMSTestsUseCase(repository)
        }
        @Provides
        fun provideLoginDao(db: AppDatabase): LoginDao = db.loginDao()

        @Provides
        fun provideLoginLocalDataSource(
            dao: LoginDao
        ): LoginLocalDataSource = LoginLocalDataSourceImpl(dao)

        @Provides
        fun provideLoginRepository(
            source: LoginLocalDataSource
        ): LoginRepository = LoginRepositoryImpl(source)

    }
}
