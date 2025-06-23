package com.gyvacha.androidssh.di

import com.gyvacha.androidssh.data.CommandExecutor
import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import com.gyvacha.androidssh.data.repository.HostRepositoryImpl
import com.gyvacha.androidssh.data.repository.SshKeyRepositoryImpl
import com.gyvacha.androidssh.domain.repository.HostRepository
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import com.gyvacha.androidssh.domain.usecase.ExecuteCommandUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostsUseCase
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.domain.usecase.InsertSshKeyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHostRepository(hostDao: HostDao): HostRepository = HostRepositoryImpl(hostDao)

    @Provides
    @Singleton
    fun provideCommandExecutor(): CommandExecutor = CommandExecutor()

    @Provides
    @Singleton
    fun provideExecuteCommandUseCase(executor: CommandExecutor): ExecuteCommandUseCase = ExecuteCommandUseCase(executor)

    @Provides
    fun provideGetHostsUseCase(repository: HostRepository): GetHostsUseCase = GetHostsUseCase(repository)

    @Provides
    fun provideInsertHostUseCase(repository: HostRepository): InsertHostUseCase = InsertHostUseCase(repository)

    @Provides
    fun provideGetSshKeysUseCase(repository: SshKeyRepository): GetSshKeysUseCase = GetSshKeysUseCase(repository)

    @Provides
    fun provideInsertSshKeyUseCase(repository: SshKeyRepository): InsertSshKeyUseCase = InsertSshKeyUseCase(repository)
}