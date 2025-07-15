package com.gyvacha.androidssh.di

import android.content.Context
import com.gyvacha.androidssh.data.CommandExecutor
import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.data.local.dao.ProxyConfigDao
import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import com.gyvacha.androidssh.data.repository.HostRepositoryImpl
import com.gyvacha.androidssh.data.repository.ProxyConfigRepositoryImpl
import com.gyvacha.androidssh.data.repository.SingboxRepositoryImpl
import com.gyvacha.androidssh.data.repository.SshKeyRepositoryImpl
import com.gyvacha.androidssh.data.repository.SshRepositoryImpl
import com.gyvacha.androidssh.domain.repository.HostRepository
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository
import com.gyvacha.androidssh.domain.repository.SingboxRepository
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import com.gyvacha.androidssh.domain.repository.SshRepository
import com.gyvacha.androidssh.domain.usecase.DeleteConfigUseCase
import com.gyvacha.androidssh.domain.usecase.ExecuteCommandUseCase
import com.gyvacha.androidssh.domain.usecase.GenerateSingboxConfigFileUseCase
import com.gyvacha.androidssh.domain.usecase.GenerateSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.GetActiveConfigUseCase
import com.gyvacha.androidssh.domain.usecase.GetConfigsUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostWithSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostsUseCase
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import com.gyvacha.androidssh.domain.usecase.InsertConfigUseCase
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.domain.usecase.InsertSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.ObserveSingboxLogsUseCase
import com.gyvacha.androidssh.domain.usecase.ObserveSingboxStateUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaKeyUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaPwdUseCase
import com.gyvacha.androidssh.domain.usecase.SshDisconnectUseCase
import com.gyvacha.androidssh.domain.usecase.SshExecuteCommandUseCase
import com.gyvacha.androidssh.domain.usecase.StopSingboxUseCase
import com.gyvacha.androidssh.domain.usecase.UpdateConfigUseCase
import com.gyvacha.androidssh.domain.usecase.UpdateHostUseCase
import com.gyvacha.androidssh.utils.SingboxConfigFileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideSingboxRepository(@ApplicationContext context: Context): SingboxRepository = SingboxRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideProxyConfigRepository(proxyConfigDao: ProxyConfigDao): ProxyConfigRepository = ProxyConfigRepositoryImpl(proxyConfigDao)

    @Provides
    @Singleton
    fun provideCommandExecutor(): CommandExecutor = CommandExecutor()

    @Provides
    fun provideStartSingboxUseCase(repository: ProxyConfigRepository): GetActiveConfigUseCase = GetActiveConfigUseCase(repository)

    @Provides
    fun provideInsertConfigUseCase(repository: ProxyConfigRepository): InsertConfigUseCase = InsertConfigUseCase(repository)

    @Provides
    fun provideUpdateConfigUseCase(repository: ProxyConfigRepository): UpdateConfigUseCase = UpdateConfigUseCase(repository)

    @Provides
    fun provideGetConfigsUseCase(repository: ProxyConfigRepository): GetConfigsUseCase = GetConfigsUseCase(repository)

    @Provides
    fun provideDeleteConfigUseCase(repository: ProxyConfigRepository): DeleteConfigUseCase = DeleteConfigUseCase(repository)

    @Provides
    fun provideGenerateSingboxConfigFileUseCase(repository: ProxyConfigRepository, fileManager: SingboxConfigFileManager): GenerateSingboxConfigFileUseCase = GenerateSingboxConfigFileUseCase(repository, fileManager)

    @Provides
    fun provideStopSingboxUseCase(repository: SingboxRepository): StopSingboxUseCase = StopSingboxUseCase(repository)

    @Provides
    fun provideObserveSingboxLogsUseCase(repository: SingboxRepository): ObserveSingboxLogsUseCase = ObserveSingboxLogsUseCase(repository)

    @Provides
    fun provideObserveSingboxStateUseCase(repository: SingboxRepository): ObserveSingboxStateUseCase = ObserveSingboxStateUseCase(repository)

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

    @Provides
    @Singleton
    fun provideSingboxConfigFileManager(@ApplicationContext context: Context): SingboxConfigFileManager = SingboxConfigFileManager(context)
}