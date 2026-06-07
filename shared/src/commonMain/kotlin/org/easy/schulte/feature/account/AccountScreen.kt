package org.easy.schulte.feature.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.easy.schulte.core.model.AccountMessage
import org.easy.schulte.core.model.Gender
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.currentUser
import org.easy.schulte.core.model.isLoggedIn
import org.easy.schulte.core.model.unlinkedLocalRecordCount
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.FocusTeal
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.KeyValueRow
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.SectionTitle
import org.easy.schulte.core.ui.StatCard
import org.easy.schulte.core.ui.WarningAmber
import org.easy.schulte.core.ui.formatSecondsText
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileRoot(
  onBack: () -> Unit,
  onOpenLogin: () -> Unit,
  onOpenRegister: () -> Unit,
  onOpenEditProfile: () -> Unit,
  onOpenAccountSettings: () -> Unit,
  onOpenRecords: () -> Unit,
  onOpenAiSettings: () -> Unit,
  onOpenPkSoon: () -> Unit,
  onContinueTraining: () -> Unit,
  viewModel: AccountViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  AccountEvents(viewModel, onBack, onOpenLogin, onOpenRegister, onOpenEditProfile, onOpenAccountSettings, onOpenRecords, onOpenAiSettings, onOpenPkSoon, onContinueTraining)
  AccountDialogs(state, viewModel::onAction)
  ProfileScreen(state, viewModel::onAction)
}

@Composable
internal fun LoginRoot(
  onBack: () -> Unit,
  onOpenRegister: () -> Unit,
  onOpenProfile: () -> Unit,
  viewModel: AccountViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  AccountEvents(
    viewModel = viewModel,
    onBack = onBack,
    onOpenLogin = {},
    onOpenRegister = onOpenRegister,
    onOpenEditProfile = {},
    onOpenAccountSettings = {},
    onOpenRecords = {},
    onOpenAiSettings = {},
    onOpenPkSoon = {},
    onContinueTraining = {},
    onOpenProfile = onOpenProfile,
  )
  AccountDialogs(state, viewModel::onAction)
  LoginScreen(state, viewModel::onAction)
}

@Composable
internal fun RegisterRoot(
  onBack: () -> Unit,
  onOpenLogin: () -> Unit,
  onOpenProfile: () -> Unit,
  viewModel: AccountViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  AccountEvents(
    viewModel = viewModel,
    onBack = onBack,
    onOpenLogin = onOpenLogin,
    onOpenRegister = {},
    onOpenEditProfile = {},
    onOpenAccountSettings = {},
    onOpenRecords = {},
    onOpenAiSettings = {},
    onOpenPkSoon = {},
    onContinueTraining = {},
    onOpenProfile = onOpenProfile,
  )
  AccountDialogs(state, viewModel::onAction)
  RegisterScreen(state, viewModel::onAction)
}

@Composable
internal fun EditProfileRoot(
  onBack: () -> Unit,
  viewModel: AccountViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  AccountEvents(viewModel, onBack, {}, {}, {}, {}, {}, {}, {}, {}, onBack)
  EditProfileScreen(state, viewModel::onAction)
}

@Composable
internal fun AccountSettingsRoot(
  onBack: () -> Unit,
  viewModel: AccountViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  AccountEvents(viewModel, onBack, {}, {}, {}, {}, {}, {}, {}, {}, onBack)
  AccountDialogs(state, viewModel::onAction)
  AccountSettingsScreen(state, viewModel::onAction)
}

@Composable
internal fun PkSoonRoot(
  onBack: () -> Unit,
  viewModel: AccountViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  AccountEvents(viewModel, onBack, {}, {}, {}, {}, {}, {}, {}, {}, onBack)
  PkSoonScreen(state, viewModel::onAction)
}

@Composable
private fun AccountEvents(
  viewModel: AccountViewModel,
  onBack: () -> Unit,
  onOpenLogin: () -> Unit,
  onOpenRegister: () -> Unit,
  onOpenEditProfile: () -> Unit,
  onOpenAccountSettings: () -> Unit,
  onOpenRecords: () -> Unit,
  onOpenAiSettings: () -> Unit,
  onOpenPkSoon: () -> Unit,
  onContinueTraining: () -> Unit,
  onOpenProfile: () -> Unit = {},
) {
  LaunchedEffect(viewModel) {
    viewModel.events.collect { event ->
      when (event) {
        AccountEvent.Back -> onBack()
        AccountEvent.OpenLogin -> onOpenLogin()
        AccountEvent.OpenRegister -> onOpenRegister()
        AccountEvent.OpenEditProfile -> onOpenEditProfile()
        AccountEvent.OpenAccountSettings -> onOpenAccountSettings()
        AccountEvent.OpenRecords -> onOpenRecords()
        AccountEvent.OpenAiSettings -> onOpenAiSettings()
        AccountEvent.OpenPkSoon -> onOpenPkSoon()
        AccountEvent.ContinueTraining -> onContinueTraining()
        AccountEvent.OpenProfile -> onOpenProfile()
      }
    }
  }
}

@Composable
internal fun ProfileScreen(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  SchulteScaffold(title = "我的", onNavigationClick = { onAction(AccountAction.Back) }) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      AccountMessageText(state.accountMessage, onAction)
      if (!state.isLoggedIn) {
        GuestProfile(onAction)
      } else {
        SignedInProfile(state, onAction)
      }
    }
  }
}

@Composable
private fun GuestProfile(onAction: (AccountAction) -> Unit) {
  SchulteCard {
    Avatar("未", modifier = Modifier.align(Alignment.CenterHorizontally))
    Text("登录后管理你的训练数据", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("注册账号后，可查看个人资料、保存训练记录，并为后续线上 PK、段位和排行榜做准备。", color = QuietText, lineHeight = 21.sp)
    Button(
      onClick = { onAction(AccountAction.OpenLogin) },
      modifier = Modifier.fillMaxWidth().height(50.dp),
      colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
      shape = RoundedCornerShape(8.dp),
    ) { Text("登录") }
    OutlinedButton(
      onClick = { onAction(AccountAction.OpenRegister) },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(8.dp),
    ) { Text("注册账号") }
    TextButton(onClick = { onAction(AccountAction.ContinueTraining) }, modifier = Modifier.fillMaxWidth()) {
      Text("暂不登录，继续训练")
    }
  }
  InfoCard(
    title = "账号能力",
    body = "训练记录归属 · 个人资料 · 段位与胜率占位 · 线上 PK 预留。不登录也可以正常训练和查看本地记录。",
  )
}

@Composable
private fun SignedInProfile(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  val user = state.currentUser ?: return
  SchulteCard {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
      Avatar(user.nickname.take(1))
      Column(modifier = Modifier.weight(1f)) {
        Text(user.nickname, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("ID: ${user.registerId}", color = QuietText)
        Text("性别：${user.gender.label()} · 注册时间：${user.createdAt.dateText()}", color = QuietText, fontSize = 13.sp)
      }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      OutlinedButton(onClick = { onAction(AccountAction.OpenEditProfile) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
        Text("编辑资料")
      }
      OutlinedButton(onClick = { onAction(AccountAction.CopyRegisterId) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
        Text("复制 ID")
      }
    }
  }
  TrainingStatsCard(state)
  CompetitionCard(state)
  SchulteCard {
    SectionTitle("功能入口")
    EntryButton("训练记录", "查看历史训练和进步情况") { onAction(AccountAction.OpenRecords) }
    EntryButton("线上 PK", "即将上线") { onAction(AccountAction.OpenPkSoon) }
    EntryButton("AI 设置", "配置增强分析能力") { onAction(AccountAction.OpenAiSettings) }
    EntryButton("账号设置", "登录状态和本地记录关联") { onAction(AccountAction.OpenAccountSettings) }
    EntryButton("关于与隐私", "训练记录默认保存在本地设备") {}
  }
}

@Composable
private fun TrainingStatsCard(state: SchulteState) {
  val summary = state.recordSummary
  SchulteCard {
    SectionTitle("训练数据")
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      StatCard("总训练次数", "${summary.totalCount} 次", Modifier.weight(1f))
      StatCard("最近 7 天", "${summary.recent7DaysCount} 次", Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      StatCard("当前最佳", summary.bestRecord?.let { formatSecondsText(it.elapsedTimeMillis) } ?: "暂无", Modifier.weight(1f))
      StatCard("最近一次", summary.latestRecord?.createdAt?.dateText() ?: "暂无", Modifier.weight(1f))
    }
    KeyValueRow("标准模式最佳", summary.bestRecord?.let { formatSecondsText(it.elapsedTimeMillis) } ?: "暂无")
    KeyValueRow("辅助模式训练", "${state.records.count { it.markMode.name == "AssistedMarking" }} 次")
  }
}

@Composable
private fun CompetitionCard(state: SchulteState) {
  val profile = state.competitiveProfile
  SchulteCard {
    SectionTitle("竞技资料")
    KeyValueRow("当前段位", profile.rankName)
    KeyValueRow("段位积分", profile.rankPoints)
    KeyValueRow("胜率", profile.winRateText)
    KeyValueRow("总场次", "${profile.totalMatches}")
    KeyValueRow("胜 / 负 / 平", "${profile.wins} / ${profile.losses} / ${profile.draws}")
    Text("线上 PK 上线后，将根据对战结果更新段位和胜率。", color = QuietText, lineHeight = 21.sp)
  }
}

@Composable
private fun LoginScreen(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  SchulteScaffold(title = "登录", onNavigationClick = { onAction(AccountAction.Back) }) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text("欢迎回来", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
      Text("登录后继续管理你的训练记录", color = QuietText)
      OutlinedTextField(
        value = state.accountForm.registerId,
        onValueChange = { onAction(AccountAction.UpdateRegisterId(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("注册 ID") },
        singleLine = true,
      )
      PasswordField("密码", state.accountForm.password) { onAction(AccountAction.UpdatePassword(it)) }
      FormError(state.accountForm.errorMessage)
      Button(
        onClick = { onAction(AccountAction.SubmitLogin) },
        enabled = state.accountForm.registerId.isNotBlank() && state.accountForm.password.isNotBlank(),
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
        shape = RoundedCornerShape(8.dp),
      ) { Text("登录") }
      TextButton(onClick = { onAction(AccountAction.OpenRegister) }, modifier = Modifier.fillMaxWidth()) {
        Text("没有账号？去注册")
      }
      Text("忘记密码暂未开放。不登录也可以正常训练。", color = QuietText, fontSize = 13.sp)
    }
  }
}

@Composable
private fun RegisterScreen(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  SchulteScaffold(title = "注册账号", onNavigationClick = { onAction(AccountAction.Back) }) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text("创建你的训练账号", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
      Text("注册后会生成唯一注册 ID，可用于后续 PK 或好友邀请", color = QuietText)
      OutlinedTextField(
        value = state.accountForm.nickname,
        onValueChange = { onAction(AccountAction.UpdateNickname(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("昵称") },
        placeholder = { Text("请输入昵称") },
        singleLine = true,
      )
      PasswordField("密码", state.accountForm.password, "至少 6 位") { onAction(AccountAction.UpdatePassword(it)) }
      PasswordField("确认密码", state.accountForm.confirmPassword) { onAction(AccountAction.UpdateConfirmPassword(it)) }
      GenderSelector(state.accountForm.gender) { onAction(AccountAction.SelectGender(it)) }
      InfoCard("注册 ID", "注册 ID 将由系统自动生成，注册后不可修改。")
      Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
          checked = state.accountForm.agreementAccepted,
          onCheckedChange = { onAction(AccountAction.AcceptAgreement(it)) },
        )
        Text("我已阅读并同意用户协议和隐私说明", color = QuietText)
      }
      FormError(state.accountForm.errorMessage)
      Button(
        onClick = { onAction(AccountAction.SubmitRegister) },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
        shape = RoundedCornerShape(8.dp),
      ) { Text("完成注册") }
      TextButton(onClick = { onAction(AccountAction.OpenLogin) }, modifier = Modifier.fillMaxWidth()) {
        Text("已有账号？去登录")
      }
    }
  }
}

@Composable
private fun EditProfileScreen(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  val user = state.currentUser ?: return
  SchulteScaffold(title = "编辑资料", onNavigationClick = { onAction(AccountAction.Back) }) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Avatar(user.nickname.take(1), modifier = Modifier.align(Alignment.CenterHorizontally))
      OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), enabled = false) {
        Text("更换头像")
      }
      OutlinedTextField(
        value = state.accountForm.nickname.ifBlank { user.nickname },
        onValueChange = { onAction(AccountAction.UpdateNickname(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("昵称") },
        singleLine = true,
      )
      GenderSelector(if (state.accountForm.nickname.isBlank()) user.gender else state.accountForm.gender) { onAction(AccountAction.SelectGender(it)) }
      SchulteCard {
        KeyValueRow("注册 ID", "${user.registerId}，不可修改")
        KeyValueRow("当前段位", "未定级")
        KeyValueRow("当前胜率", "暂无对战数据")
      }
      FormError(state.accountForm.errorMessage)
      Button(
        onClick = { onAction(AccountAction.SaveProfile) },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
        shape = RoundedCornerShape(8.dp),
      ) { Text("保存资料") }
    }
  }
}

@Composable
private fun AccountSettingsScreen(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  val user = state.currentUser ?: return
  SchulteScaffold(title = "账号设置", onNavigationClick = { onAction(AccountAction.Back) }) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      AccountMessageText(state.accountMessage, onAction)
      SchulteCard {
        SectionTitle("当前账号")
        KeyValueRow("昵称", user.nickname)
        KeyValueRow("注册 ID", user.registerId)
        KeyValueRow("登录状态", "已登录")
      }
      SchulteCard {
        SectionTitle("账号操作")
        EntryButton("修改密码", "后续开放") {}
        EntryButton("关联本地训练记录", if (state.unlinkedLocalRecordCount > 0) "发现 ${state.unlinkedLocalRecordCount} 条本地记录" else "暂无可关联记录") {
          onAction(AccountAction.RequestLinkLocalRecords)
        }
      }
      InfoCard("安全与隐私", "退出登录不会删除本地训练记录。用户协议、隐私说明和注销账号将在后续版本补齐。")
      OutlinedButton(
        onClick = { onAction(AccountAction.RequestLogout) },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp),
      ) { Text("退出登录", color = WarningAmber) }
    }
  }
}

@Composable
private fun PkSoonScreen(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  SchulteScaffold(title = "线上 PK 即将上线", onNavigationClick = { onAction(AccountAction.Back) }) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text("未来你可以与其他用户进行舒尔特方格速度对战。", color = QuietText, lineHeight = 21.sp)
      InfoCard("未来能力", "实时匹配 · 好友邀请 · 段位积分 · 胜率统计 · 赛季记录")
      CompetitionCard(state)
      InfoCard("规则预告", "双方使用相同规格和模式完成训练，根据完成时间、错误次数综合判定胜负。正式规则以后续版本为准。")
      Button(
        onClick = { onAction(AccountAction.Back) },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FocusTeal),
        shape = RoundedCornerShape(8.dp),
      ) { Text("返回我的") }
    }
  }
}

@Composable
private fun AccountDialogs(
  state: SchulteState,
  onAction: (AccountAction) -> Unit,
) {
  if (state.showLinkLocalRecordsDialog) {
    AlertDialog(
      onDismissRequest = { onAction(AccountAction.DismissLinkLocalRecords) },
      title = { Text("关联本地训练记录？") },
      text = { Text("发现 ${state.unlinkedLocalRecordCount} 条本地训练记录。关联后，这些记录将归属到当前账号，并用于个人统计和后续训练分析。") },
      confirmButton = {
        TextButton(onClick = { onAction(AccountAction.LinkLocalRecords) }) {
          Text("立即关联")
        }
      },
      dismissButton = {
        TextButton(onClick = { onAction(AccountAction.DismissLinkLocalRecords) }) {
          Text("暂不关联")
        }
      },
    )
  }
  if (state.showLogoutDialog) {
    AlertDialog(
      onDismissRequest = { onAction(AccountAction.CancelLogout) },
      title = { Text("确认退出登录？") },
      text = { Text("退出后不会删除本地训练记录。你仍然可以继续训练，但账号资料和后续 PK 功能需要重新登录后使用。") },
      confirmButton = {
        TextButton(onClick = { onAction(AccountAction.ConfirmLogout) }) {
          Text("确认退出", color = WarningAmber)
        }
      },
      dismissButton = {
        TextButton(onClick = { onAction(AccountAction.CancelLogout) }) {
          Text("取消")
        }
      },
    )
  }
}

@Composable
private fun PasswordField(
  label: String,
  value: String,
  placeholder: String = "",
  onValueChange: (String) -> Unit,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = Modifier.fillMaxWidth(),
    label = { Text(label) },
    placeholder = if (placeholder.isNotBlank()) ({ Text(placeholder) }) else null,
    visualTransformation = PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    singleLine = true,
  )
}

@Composable
private fun GenderSelector(
  selected: Gender,
  onSelected: (Gender) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    SectionTitle("性别")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Gender.entries.forEach { gender ->
        FilterChip(
          selected = selected == gender,
          onClick = { onSelected(gender) },
          label = { Text(gender.label()) },
        )
      }
    }
  }
}

@Composable
private fun EntryButton(
  title: String,
  subtitle: String,
  onClick: () -> Unit,
) {
  OutlinedButton(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
  ) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(title, fontWeight = FontWeight.SemiBold)
      Text(subtitle, color = QuietText)
    }
  }
}

@Composable
private fun Avatar(
  text: String,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .size(58.dp)
      .background(Color(0xFFEAF4FF), CircleShape)
      .border(1.dp, Color(0xFFD7E6FA), CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    Text(text.ifBlank { "我" }, color = FocusBlue, fontWeight = FontWeight.Bold, fontSize = 22.sp)
  }
}

@Composable
private fun FormError(message: String?) {
  AnimatedVisibility(message != null) {
    Text(message.orEmpty(), color = WarningAmber)
  }
}

@Composable
private fun AccountMessageText(
  message: AccountMessage?,
  onAction: (AccountAction) -> Unit,
) {
  AnimatedVisibility(message != null) {
    SchulteCard {
      Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(message?.label().orEmpty(), color = FocusTeal, fontWeight = FontWeight.SemiBold)
        TextButton(onClick = { onAction(AccountAction.ClearMessage) }) {
          Text("知道了")
        }
      }
    }
  }
}

private fun Gender.label(): String = when (this) {
  Gender.Male -> "男"
  Gender.Female -> "女"
  Gender.Private -> "不透露"
}

private fun AccountMessage.label(): String = when (this) {
  AccountMessage.Registered -> "注册成功，已自动登录"
  AccountMessage.LoggedIn -> "登录成功"
  AccountMessage.ProfileSaved -> "资料已保存"
  AccountMessage.LocalRecordsLinked -> "本地记录已关联到当前账号"
  AccountMessage.LoggedOut -> "已退出登录"
  AccountMessage.RegisterIdCopied -> "注册 ID 已复制"
}

private fun Long.dateText(): String {
  val days = ((org.easy.schulte.core.platform.currentTimeMillis() - this) / (24L * 60 * 60 * 1000)).coerceAtLeast(0)
  return when (days) {
    0L -> "今天"
    1L -> "昨天"
    else -> "$days 天前"
  }
}
