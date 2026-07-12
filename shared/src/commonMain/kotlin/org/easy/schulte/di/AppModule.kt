package org.easy.schulte.di

import org.easy.schulte.core.data.AccountRepository
import org.easy.schulte.core.data.AiAnalysisRepository
import org.easy.schulte.core.data.ConfigurationRepository
import org.easy.schulte.core.data.ConfigurationStore
import org.easy.schulte.core.data.DatabaseDriverFactory
import org.easy.schulte.core.data.InMemorySchulteRepository
import org.easy.schulte.core.data.SchulteDatabaseProvider
import org.easy.schulte.core.data.SettingsRepository
import org.easy.schulte.core.data.SqlDelightConfigurationStore
import org.easy.schulte.core.data.SqlDelightTrainingRecordStore
import org.easy.schulte.core.data.TrainingRecordStore
import org.easy.schulte.core.data.TrainingRecordsRepository
import org.easy.schulte.core.data.TrainingRepository
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
import org.easy.schulte.feature.training.TrainingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun appModule(
  databaseDriverFactory: DatabaseDriverFactory,
  secureSecretStore: SecureSecretStore,
) = module {
  single { SchulteDatabaseProvider(databaseDriverFactory) }
  single<SecureSecretStore> { secureSecretStore }
  single<AccountSessionStore> { SecureAccountSessionStore(get()) }
  single { AiApiKeyStore(get()) }
  single { createSchulteHttpClient() }
  single { AccountApiConfig() }
  single { AccountApi(get(), get()) }
  single<TrainingRecordStore> { SqlDelightTrainingRecordStore(get()) }
  single<ConfigurationStore> { SqlDelightConfigurationStore(get()) }
  single { InMemorySchulteRepository(get(), get(), get(), get(), get()) }
  single<ConfigurationRepository> { get<InMemorySchulteRepository>() }
  single<TrainingRepository> { get<InMemorySchulteRepository>() }
  single<SettingsRepository> { get<InMemorySchulteRepository>() }
  single<TrainingRecordsRepository> { get<InMemorySchulteRepository>() }
  single<AccountRepository> { get<InMemorySchulteRepository>() }
  single<AiAnalysisRepository> { get<InMemorySchulteRepository>() }
  singleOf(::TrainingReportCalculator)
  singleOf(::AiAnalysisGenerator)
  viewModelOf(::ConfigViewModel)
  viewModelOf(::TrainingViewModel)
  viewModelOf(::ReportViewModel)
  viewModelOf(::AdviceViewModel)
  viewModelOf(::AccountViewModel)
  viewModelOf(::SettingsViewModel)
  viewModelOf(::TrainingRecordsViewModel)
}
