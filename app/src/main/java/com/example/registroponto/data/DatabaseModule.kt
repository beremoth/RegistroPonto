package com.example.registroponto.data

import AppDatabase
import android.content.Context
import androidx.room.Room
import RegistroPontoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideAppDatabase(@ApplicationContext c: Context): AppDatabase =
        Room.databaseBuilder(c, AppDatabase::class.java, "registro_ponto_db").build()

    @Provides
    fun provideRegistroDao(db: AppDatabase): RegistroPontoDao = db.registroPontoDao()
}

