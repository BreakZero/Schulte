package org.easy.schulte.feature.account

internal sealed interface AccountEvent {
  data object Back : AccountEvent
  data object OpenLogin : AccountEvent
  data object OpenRegister : AccountEvent
  data object OpenEditProfile : AccountEvent
  data object OpenAccountSettings : AccountEvent
  data object OpenRecords : AccountEvent
  data object OpenAiSettings : AccountEvent
  data object OpenPkSoon : AccountEvent
  data object ContinueTraining : AccountEvent
  data object OpenProfile : AccountEvent
}
