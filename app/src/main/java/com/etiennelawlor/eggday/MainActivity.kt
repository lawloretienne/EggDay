package com.etiennelawlor.eggday

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
                // Padding outside the scroll so content stops below the status bar.
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                EggHero(today)
                PackDateChecker(today)
                HowToCard()
                AddWidgetButton()
            }
        }
    }
}

@Composable
private fun EggHero(today: LocalDate) {
    val number = formatDayOfYear(today.dayOfYear)
    // Settles in with a small bounce. Compose skips it when animations are turned off.
    val scale = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow))
    }
    val description = stringResource(R.string.egg_description, number)

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.today_label).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        StampedEgg(
            number = number,
            height = 220.dp,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    transformOrigin = TransformOrigin(0.5f, 0.95f)
                }
                .semantics { contentDescription = description },
        )
        Text(
            text = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/** The egg drawable with [number] laid over it in dot-matrix digits. */
@Composable
private fun StampedEgg(number: String, height: Dp, modifier: Modifier = Modifier) {
    val digitHeight = height * EggArt.DIGIT_HEIGHT
    Box(
        modifier = modifier.size(height * EggArt.ASPECT, height),
        contentAlignment = Alignment.Center,
    ) {
        Image(painterResource(R.drawable.egg), contentDescription = null, modifier = Modifier.fillMaxSize())
        Row(
            modifier = Modifier.offset(y = height * EggArt.STAMP_OFFSET),
            horizontalArrangement = Arrangement.spacedBy(digitHeight * EggArt.DIGIT_GAP),
        ) {
            number.forEach { digit ->
                Image(
                    painter = painterResource(EggArt.DIGITS[digit - '0']),
                    contentDescription = null,
                    modifier = Modifier.size(digitHeight * EggArt.DIGIT_ASPECT, digitHeight),
                )
            }
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
    val edge = MaterialTheme.colorScheme.outline
    val shape = RoundedCornerShape(16.dp)

    // Styled like the printed end of a carton: board-colored, with a dashed print border.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .drawBehind {
                val inset = 6.dp.toPx()
                drawRoundRect(
                    color = edge,
                    topLeft = Offset(inset, inset),
                    size = size.copy(width = size.width - 2 * inset, height = size.height - 2 * inset),
                    cornerRadius = CornerRadius(10.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                    ),
                )
            }
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_egg),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.checker_title), style = MaterialTheme.typography.titleLarge)
        }
        Text(
            text = stringResource(R.string.checker_label).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = input,
            onValueChange = { new -> input = new.filter(Char::isDigit).take(3) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 28.sp, letterSpacing = 6.sp),
            placeholder = {
                // Faded so today's number reads as an example, not an entry.
                Text(
                    text = formatDayOfYear(today.dayOfYear),
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 28.sp, letterSpacing = 6.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
            },
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
            WeeksScale(days)
        }
    }
}

private const val SCALE_WEEKS = 6

/** A 0–6 week track with an egg marking how long ago the eggs were packed. */
@Composable
private fun WeeksScale(days: Int) {
    val fraction = (days / 7f / SCALE_WEEKS).coerceIn(0f, 1f)
    val track = MaterialTheme.colorScheme.outline
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = labelColor)
    val measurer = rememberTextMeasurer()
    val markerSize = 22.dp
    val endLabel = stringResource(R.string.weeks_scale_end)

    Column(
        modifier = Modifier.clearAndSetSemantics { },
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(stringResource(R.string.weeks_scale_label), style = MaterialTheme.typography.labelMedium, color = labelColor)
        BoxWithConstraints(Modifier.fillMaxWidth().height(56.dp)) {
            // Inset the track by half a marker so the egg stays inside at both ends.
            val inset = markerSize / 2
            Canvas(Modifier.fillMaxSize()) {
                val left = inset.toPx()
                val right = size.width - inset.toPx()
                val y = markerSize.toPx() + 6.dp.toPx()
                drawLine(track, Offset(left, y), Offset(right, y), strokeWidth = 2.dp.toPx())
                for (week in 0..SCALE_WEEKS) {
                    val x = left + (right - left) * week / SCALE_WEEKS
                    drawLine(track, Offset(x, y - 4.dp.toPx()), Offset(x, y + 4.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                    val label = measurer.measure(if (week == SCALE_WEEKS) endLabel else "$week", labelStyle)
                    drawText(label, topLeft = Offset(x - label.size.width / 2f, y + 8.dp.toPx()))
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_egg),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .offset(x = (maxWidth - markerSize) * fraction)
                    .size(markerSize),
            )
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(stringResource(R.string.howto_title), style = MaterialTheme.typography.titleMedium)
            Image(
                painter = painterResource(R.drawable.carton_end),
                contentDescription = stringResource(R.string.carton_description),
                modifier = Modifier.fillMaxWidth().aspectRatio(240f / 112f),
            )
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
        Icon(painterResource(R.drawable.ic_egg), contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
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
