package org.easy.schulte.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SchulteRepository
import org.easy.schulte.core.model.isLoggedIn
import org.easy.schulte.state.accountStateIn

internal class AccountViewModel(
  private val repository: SchulteRepository,
) : ViewModel() {
  val state = repository.accountStateIn(viewModelScope)

  private val _events = Channel<AccountEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: AccountAction) {
    when (action) {
      AccountAction.Back -> sendEvent(AccountEvent.Back)
      AccountAction.OpenLogin -> {
        repository.clearAccountForm()
        sendEvent(AccountEvent.OpenLogin)
      }
      AccountAction.OpenRegister -> {
        repository.clearAccountForm()
        sendEvent(AccountEvent.OpenRegister)
      }
      AccountAction.OpenEditProfile -> {
        repository.clearAccountForm()
        sendEvent(AccountEvent.OpenEditProfile)
      }
      AccountAction.OpenAccountSettings -> sendEvent(AccountEvent.OpenAccountSettings)
      AccountAction.OpenRecords -> sendEvent(AccountEvent.OpenRecords)
      AccountAction.OpenAiSettings -> sendEvent(AccountEvent.OpenAiSettings)
      AccountAction.OpenPkSoon -> {
        if (repository.currentState().isLoggedIn) {
          sendEvent(AccountEvent.OpenPkSoon)
        } else {
          repository.clearAccountForm()
          sendEvent(AccountEvent.OpenLogin)
        }
      }
      AccountAction.ContinueTraining -> sendEvent(AccountEvent.ContinueTraining)
      is AccountAction.UpdateRegisterId -> repository.updateLoginRegisterId(action.value)
      is AccountAction.UpdateNickname -> repository.updateAccountNickname(action.value)
      is AccountAction.UpdatePassword -> repository.updateAccountPassword(action.value)
      is AccountAction.UpdateConfirmPassword -> repository.updateAccountConfirmPassword(action.value)
      is AccountAction.SelectGender -> repository.updateAccountGender(action.gender)
      is AccountAction.AcceptAgreement -> repository.updateAgreementAccepted(action.accepted)
      AccountAction.SubmitLogin -> {
        repository.loginAccount()
        if (repository.currentState().isLoggedIn) sendEvent(AccountEvent.OpenProfile)
      }
      AccountAction.SubmitRegister -> {
        repository.registerAccount()
        if (repository.currentState().isLoggedIn) sendEvent(AccountEvent.OpenProfile)
      }
      AccountAction.SaveProfile -> {
        repository.updateCurrentProfile()
        if (repository.currentState().accountForm.errorMessage == null) sendEvent(AccountEvent.Back)
      }
      AccountAction.CopyRegisterId -> repository.markRegisterIdCopied()
      AccountAction.RequestLinkLocalRecords -> repository.requestLinkLocalRecords()
      AccountAction.LinkLocalRecords -> repository.linkLocalRecords()
      AccountAction.DismissLinkLocalRecords -> repository.dismissLinkLocalRecords()
      AccountAction.RequestLogout -> repository.requestLogout()
      AccountAction.CancelLogout -> repository.cancelLogout()
      AccountAction.ConfirmLogout -> repository.logout()
      AccountAction.ClearMessage -> repository.clearAccountMessage()
    }
  }

  private fun sendEvent(event: AccountEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }
}
