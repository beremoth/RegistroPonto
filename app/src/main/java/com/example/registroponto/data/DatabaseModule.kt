package com.example.registroponto.di

import AppDatabase
import android.content.Context
import androidx.room.Room
import RegistroPontoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "registro_ponto_db"
        ).build()
    }

    @Provides
    fun provideRegistroPontoDao(database: AppDatabase): RegistroPontoDao {
        return database.registroPontoDao()
    }
}
