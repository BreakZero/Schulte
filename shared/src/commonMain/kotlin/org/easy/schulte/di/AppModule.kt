package org.easy.schulte.di

import org.easy.schulte.core.data.InMemorySchulteRepository
import org.easy.schulte.core.data.SchulteRepository
import org.easy.schulte.core.domain.AiAnalysisGenerator
import org.easy.schulte.core.domain.TrainingReportCalculator
import org.easy.schulte.feature.advice.AdviceViewModel
import org.easy.schulte.feature.config.ConfigViewModel
import org.easy.schulte.feature.report.ReportViewModel
import org.easy.schulte.feature.settings.SettingsViewModel
import org.easy.schulte.feature.training.TrainingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val appModule = module {
  single<SchulteRepository> { InMemorySchulteRepository() }
  singleOf(::TrainingReportCalculator)
  singleOf(::AiAnalysisGenerator)
  viewModelOf(::ConfigViewModel)
  viewModelOf(::TrainingViewModel)
  viewModelOf(::ReportViewModel)
  viewModelOf(::AdviceViewModel)
  viewModelOf(::SettingsViewModel)
}
