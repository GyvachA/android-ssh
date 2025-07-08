package com.gyvacha.androidssh.di

import com.gyvacha.androidssh.data.CommandExecutor
import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import com.gyvacha.androidssh.data.repository.HostRepositoryImpl
import com.gyvacha.androidssh.data.repository.SshKeyRepositoryImpl
import com.gyvacha.androidssh.data.repository.SshRepositoryImpl
import com.gyvacha.androidssh.domain.repository.HostRepository
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import com.gyvacha.androidssh.domain.repository.SshRepository
import com.gyvacha.androidssh.domain.usecase.ExecuteCommandUseCase
import com.gyvacha.androidssh.domain.usecase.GenerateSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostWithSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostsUseCase
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.domain.usecase.InsertSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaKeyUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaPwdUseCase
import com.gyvacha.androidssh.domain.usecase.SshDisconnectUseCase
import com.gyvacha.androidssh.domain.usecase.SshExecuteCommandUseCase
import com.gyvacha.androidssh.domain.usecase.UpdateHostUseCase
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
    fun provideSshKeyRepository(sshKeyDao: SshKeyDao): SshKeyRepository = SshKeyRepositoryImpl(sshKeyDao)

    @Provides
    @Singleton
    fun provideCommandExecutor(): CommandExecutor = CommandExecutor()

    @Provides
    @Singleton
    fun provideExecuteCommandUseCase(executor: CommandExecutor): ExecuteCommandUseCase = ExecuteCommandUseCase(executor)

    @Provides
    fun provideGetHostsUseCase(repository: HostRepository): GetHostsUseCase = GetHostsUseCase(repository)

    @Provides
    fun provideGetHostUseCase(repository: HostRepository): GetHostUseCase = GetHostUseCase(repository)

    @Provides
    fun provideUpdateHostUseCase(repository: HostRepository): UpdateHostUseCase = UpdateHostUseCase(repository)

    @Provides
    fun provideGetHostWithSshKeyUseCase(repository: HostRepository): GetHostWithSshKeyUseCase = GetHostWithSshKeyUseCase(repository)

    @Provides
    fun provideInsertHostUseCase(repository: HostRepository): InsertHostUseCase = InsertHostUseCase(repository)

    @Provides
    fun provideGetSshKeysUseCase(repository: SshKeyRepository): GetSshKeysUseCase = GetSshKeysUseCase(repository)

    @Provides
    fun provideInsertSshKeyUseCase(repository: SshKeyRepository): InsertSshKeyUseCase = InsertSshKeyUseCase(repository)

    @Provides
    fun provideGenerateSshKeyUseCase(repository: SshKeyRepository): GenerateSshKeyUseCase = GenerateSshKeyUseCase(repository)

    @Provides
    @Singleton
    fun provideSshRepository() : SshRepository = SshRepositoryImpl()

    @Provides
    fun provideSshConnectViaKey(repository: SshRepository): SshConnectViaKeyUseCase = SshConnectViaKeyUseCase(repository)

    @Provides
    fun provideSshConnectViaPwd(repository: SshRepository): SshConnectViaPwdUseCase = SshConnectViaPwdUseCase(repository)

    @Provides
    fun provideSshExecuteCommand(repository: SshRepository): SshExecuteCommandUseCase = SshExecuteCommandUseCase(repository)

    @Provides
    fun provideSshDisconnect(repository: SshRepository): SshDisconnectUseCase = SshDisconnectUseCase(repository)
}