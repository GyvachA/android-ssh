package com.gyvacha.androidssh.di

import android.content.Context
import androidx.room.Room
import com.gyvacha.androidssh.data.local.HostDatabase
import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.data.local.dao.ProxyConfigDao
import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import com.gyvacha.androidssh.utils.DatabaseKeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabasePassphrase(@ApplicationContext context: Context): ByteArray {
        return DatabaseKeyManager.getOrCreateSecretKey(context)
    }

    @Provides
    @Singleton
    fun provideSupportFactory(passphrase: ByteArray): SupportOpenHelperFactory {
        return SupportOpenHelperFactory(passphrase)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        factory: SupportOpenHelperFactory
    ): HostDatabase {
        return Room.databaseBuilder(
            context,
            HostDatabase::class.java,
            "host_db"
        )
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    @Singleton
    fun provideHostDao(db: HostDatabase): HostDao = db.hostDao()

    @Provides
    @Singleton
    fun provideSshKeyDao(db: HostDatabase): SshKeyDao = db.sshKeyDao()

    @Provides
    @Singleton
    fun provideProxyConfigDao(db: HostDatabase): ProxyConfigDao = db.proxyConfigDao()
}