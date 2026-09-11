package com.yoga.firesafety.shared.config

import com.yoga.firesafety.shared.data.remote.FireSafetyApi
import com.yoga.firesafety.shared.data.repository.WorkOrderRepositoryImpl
import com.yoga.firesafety.shared.data.repository.SessionRepositoryImpl
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.data.local.DriverFactory
import com.yoga.firesafety.shared.db.FireSafetyDatabase
import com.yoga.firesafety.shared.db.WorkOrderEntity
import com.yoga.firesafety.shared.presentation.auth.LoginViewModel
import com.yoga.firesafety.shared.presentation.auth.SignupViewModel
import com.yoga.firesafety.shared.presentation.admin.UserManagementViewModel
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderViewModel
import com.yoga.firesafety.shared.presentation.MainViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule)
    }

val commonModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120000L
                connectTimeoutMillis = 120000L
                socketTimeoutMillis = 120000L
            }
        }
    }
    single {
        FireSafetyDatabase(get<DriverFactory>().createDriver())
    }
    single { FireSafetyApi(get()) }
    single<WorkOrderRepository> { WorkOrderRepositoryImpl(get(), get()) }
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    factory { LoginViewModel(get(), get()) }
    factory { SignupViewModel(get(), get()) }
    factory { UserManagementViewModel(get()) }
    factory { WorkOrderViewModel(get()) }
    factory { MainViewModel(get(), get()) }
}
