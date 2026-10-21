package com.etiennelawlor.eggday

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.etiennelawlor.eggday.ui.theme.EggDayTheme
import com.etiennelawlor.eggday.widget.EggDayWidgetReceiver
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Re-read on resume so the screen is right after leaving the app open past midnight.
    private var today by mutableStateOf(LocalDate.now())

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EggDayTheme {
                EggDayScreen(today)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        today = LocalDate.now()
    }
}

@Composable
private fun EggDayScreen(today: LocalDate) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TodayCard(today)
                PackDateChecker(today)
                HowToCard()
                AddWidgetButton()
            }
        }
    }
}

@Composable
private fun TodayCard(today: LocalDate) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.today_label), style = MaterialTheme.typography.titleMedium)
            Text(
                text = formatDayOfYear(today.dayOfYear),
                fontSize = 88.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 96.sp,
            )
            Text(
                text = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun PackDateChecker(today: LocalDate) {
    var input by rememberSaveable { mutableStateOf("") }
    val packDay = input.toIntOrNull()
    val packed = packDay?.let { packDate(it, today) }
    val days = packDay?.let { daysSincePack(it, today) }
    val isError = input.length == 3 && packed == null

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.checker_title), style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = input,
                onValueChange = { new -> input = new.filter(Char::isDigit).take(3) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.checker_label)) },
                placeholder = { Text(formatDayOfYear(today.dayOfYear)) },
                singleLine = true,
                isError = isError,
                supportingText = {
                    Text(stringResource(if (isError) R.string.checker_error else R.string.checker_hint))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            if (packed != null && days != null && input.isNotEmpty()) {
                val dateText = packed.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                Text(
                    text = when (days) {
                        0 -> stringResource(R.string.checker_result_today)
                        else -> pluralStringResource(R.plurals.checker_result_days, days, days, dateText)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun HowToCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.howto_title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.howto_body), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.howto_storage), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AddWidgetButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val supported = remember {
        context.getSystemService(AppWidgetManager::class.java).isRequestPinAppWidgetSupported
    }
    if (!supported) return

    Button(
        onClick = {
            scope.launch {
                GlanceAppWidgetManager(context).requestPinGlanceAppWidget(EggDayWidgetReceiver::class.java)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        Text(stringResource(R.string.add_widget))
    }
}

@Preview(showBackground = true)
@Composable
private fun EggDayScreenPreview() {
    EggDayTheme {
        EggDayScreen(LocalDate.of(2026, 10, 20))
    }
}
