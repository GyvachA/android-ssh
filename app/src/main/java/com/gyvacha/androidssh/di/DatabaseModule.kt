package com.gyvacha.androidssh.di

import android.content.Context
import androidx.room.Room
import com.gyvacha.androidssh.data.local.HostDatabase
import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HostDatabase {
        return Room.databaseBuilder(context, HostDatabase::class.java, "host_db").build()
    }

    @Provides
    @Singleton
    fun provideHostDao(db: HostDatabase): HostDao = db.hostDao()

    @Provides
    @Singleton
    fun provideSshKeyDao(db: HostDatabase): SshKeyDao = db.sshKeyDao()
}