package com.example.registroponto.di

import AppDatabase
import androidx.room.Room
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Banco de dados e DAO
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "registro_ponto_db"
        ).build()
    }
    single { get<AppDatabase>().registroPontoDao() }

    // ViewModel
    viewModel { RegistroPontoViewModel(get()) }
}
