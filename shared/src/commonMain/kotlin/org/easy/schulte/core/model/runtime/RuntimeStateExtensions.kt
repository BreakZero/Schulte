package org.easy.schulte.core.model.runtime

import org.easy.schulte.core.model.account.UserAccount

val AccountRuntimeState.currentUser: UserAccount?
  get() = accounts.firstOrNull { it.userId == currentUserId }

val AccountRuntimeState.isLoggedIn: Boolean
  get() = currentUser != null

val TrainingRecordsRuntimeState.unlinkedLocalRecordCount: Int
  get() = records.count { it.ownerUserId == null }
