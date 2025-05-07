package com.example.registroponto.data

import AppDatabase
import RegistroPontoDao
import android.content.Context
import androidx.room.Room

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "registro_ponto_db"
        ).build()
    }

    @Provides
    fun provideRegistroDao(db: AppDatabase): RegistroPontoDao {
        return db.registroPontoDao()
    }
}

