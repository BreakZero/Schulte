package org.easy.schulte.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.MarkMode
import org.jetbrains.compose.resources.painterResource
import schulte.shared.generated.resources.*

@Composable
internal fun ScoreBadge(scoreLevel: ScoreLevel) {
  val colors = MaterialTheme.colorScheme
  val statusColors = MaterialTheme.schulteStatusColors
  val color = when (scoreLevel) {
    ScoreLevel.Excellent -> statusColors.success
    ScoreLevel.Good -> colors.primary
    ScoreLevel.Pass -> statusColors.warning
    ScoreLevel.Below -> colors.error
    ScoreLevel.Practice -> colors.onSurfaceVariant
  }
  Box(
    modifier = Modifier
      .background(color.copy(alpha = 0.12f), CircleShape)
      .padding(horizontal = 14.dp, vertical = 7.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(scoreLevel.titleText(), color = color, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
internal fun InfoCard(title: String, body: String) {
  SchulteCard {
    Text(
      title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface,
    )
    Spacer(Modifier.height(6.dp))
    Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 21.sp)
  }
}

@Composable
internal fun SchulteCard(content: @Composable ColumnScope.() -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    content = {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = content,
      )
    },
  )
}

@Composable
internal fun SelectCard(
  selected: Boolean,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .height(78.dp)
      .clickable(onClick = onClick),
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(
      1.dp,
      if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
    ),
    colors = CardDefaults.cardColors(
      containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
      } else {
        MaterialTheme.colorScheme.surfaceContainerLow
      },
    ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
      Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1)
    }
  }
}

@Composable
internal fun ModeCard(
  selected: Boolean,
  mode: MarkMode,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(
      1.dp,
      if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
    ),
    colors = CardDefaults.cardColors(
      containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
      } else {
        MaterialTheme.colorScheme.surfaceContainerLow
      },
    ),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier = Modifier
          .size(18.dp)
          .background(
            if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            CircleShape,
          )
          .border(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            CircleShape,
          ),
      )
      Column {
        Text(mode.titleText(), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text(mode.descriptionText(), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
      }
    }
  }
}

@Composable
internal fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.height(76.dp),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalArrangement = Arrangement.Center,
    ) {
      Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1)
      Text(
        value,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
internal fun KeyValueRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.width(16.dp))
    Text(
      value,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.End,
    )
  }
}

@Composable
internal fun SwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
      Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}

@Composable
internal fun SectionTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    fontWeight = FontWeight.SemiBold,
    color = MaterialTheme.colorScheme.onSurface,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SchulteScaffold(
  title: String,
  onNavigationClick: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
  content: @Composable () -> Unit,
) {
  Scaffold(
    modifier = Modifier
      .fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBar = {
      TopAppBar(
        title = {
          Text(
            title,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        navigationIcon = {
          if (onNavigationClick != null) {
            IconButton(onClick = onNavigationClick) {
              Icon(
                painter = painterResource(Res.drawable.ic_arrow_back_24),
                contentDescription = null,
              )
            }
          }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
          titleContentColor = MaterialTheme.colorScheme.onBackground,
          navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
          actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
      )
    },
  ) { padding ->
    Box(Modifier.padding(padding).padding(vertical = 16.dp)) {
      content()
    }
  }
}

internal fun formatTimer(millis: Long): String {
  val totalCentis = millis / 10
  val minutes = totalCentis / 6000
  val seconds = (totalCentis / 100) % 60
  val centis = totalCentis % 100
  return "${minutes.twoDigits()}:${seconds.twoDigits()}.${centis.twoDigits()}"
}

internal fun Long.twoDigits(): String = if (this < 10) "0$this" else toString()
