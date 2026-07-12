package org.easy.schulte.di

import org.easy.schulte.core.data.AccountRepository
import org.easy.schulte.core.data.AccountRepositoryImpl
import org.easy.schulte.core.data.AiAnalysisRepository
import org.easy.schulte.core.data.AiAnalysisRepositoryImpl
import org.easy.schulte.core.data.AppDispatchers
import org.easy.schulte.core.data.ConfigurationRepository
import org.easy.schulte.core.data.ConfigurationRepositoryImpl
import org.easy.schulte.core.data.ConfigurationStore
import org.easy.schulte.core.data.DatabaseDriverFactory
import org.easy.schulte.core.data.SchulteDatabaseProvider
import org.easy.schulte.core.data.SchulteSharedState
import org.easy.schulte.core.data.SettingsRepository
import org.easy.schulte.core.data.SettingsRepositoryImpl
import org.easy.schulte.core.data.SqlDelightConfigurationStore
import org.easy.schulte.core.data.SqlDelightTrainingRecordStore
import org.easy.schulte.core.data.TrainingRecordStore
import org.easy.schulte.core.data.TrainingRecordsRepository
import org.easy.schulte.core.data.TrainingRecordsRepositoryImpl
import org.easy.schulte.core.data.TrainingRepository
import org.easy.schulte.core.data.TrainingRepositoryImpl
import org.easy.schulte.core.domain.AiAnalysisGenerator
import org.easy.schulte.core.domain.TrainingReportCalculator
import org.easy.schulte.core.network.AccountApi
import org.easy.schulte.core.network.AccountApiConfig
import org.easy.schulte.core.network.createSchulteHttpClient
import org.easy.schulte.core.security.AccountSessionStore
import org.easy.schulte.core.security.AiApiKeyStore
import org.easy.schulte.core.security.SecureAccountSessionStore
import org.easy.schulte.core.security.SecureSecretStore
import org.easy.schulte.feature.account.AccountViewModel
import org.easy.schulte.feature.advice.AdviceViewModel
import org.easy.schulte.feature.config.ConfigViewModel
import org.easy.schulte.feature.records.TrainingRecordsViewModel
import org.easy.schulte.feature.report.ReportViewModel
import org.easy.schulte.feature.settings.SettingsViewModel
import org.easy.schulte.feature.training.TrainingExecutionContext
import org.easy.schulte.feature.training.TrainingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun appModule(
  databaseDriverFactory: DatabaseDriverFactory,
  secureSecretStore: SecureSecretStore,
) = module {
  single { SchulteDatabaseProvider(databaseDriverFactory) }
  single { AppDispatchers() }
  single<SecureSecretStore> { secureSecretStore }
  single<AccountSessionStore> { SecureAccountSessionStore(get()) }
  single { AiApiKeyStore(get()) }
  single { createSchulteHttpClient() }
  single { AccountApiConfig() }
  single { AccountApi(get(), get()) }
  single<TrainingRecordStore> { SqlDelightTrainingRecordStore(get(), get()) }
  single<ConfigurationStore> { SqlDelightConfigurationStore(get(), get()) }
  single { SchulteSharedState(get(), get(), get(), get()) }
  single<ConfigurationRepository> { ConfigurationRepositoryImpl(get()) }
  single<TrainingRepository> { TrainingRepositoryImpl(get()) }
  single<SettingsRepository> { SettingsRepositoryImpl(get()) }
  single<TrainingRecordsRepository> { TrainingRecordsRepositoryImpl(get()) }
  single<AccountRepository> { AccountRepositoryImpl(get(), get(), get()) }
  single<AiAnalysisRepository> { AiAnalysisRepositoryImpl(get()) }
  singleOf(::TrainingReportCalculator)
  single { TrainingExecutionContext() }
  singleOf(::AiAnalysisGenerator)
  viewModelOf(::ConfigViewModel)
  viewModelOf(::TrainingViewModel)
  viewModelOf(::ReportViewModel)
  viewModelOf(::AdviceViewModel)
  viewModelOf(::AccountViewModel)
  viewModelOf(::SettingsViewModel)
  viewModelOf(::TrainingRecordsViewModel)
}
