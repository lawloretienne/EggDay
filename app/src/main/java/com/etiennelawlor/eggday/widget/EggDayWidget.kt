package com.etiennelawlor.eggday.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.etiennelawlor.eggday.EggArt
import com.etiennelawlor.eggday.MainActivity
import com.etiennelawlor.eggday.R
import com.etiennelawlor.eggday.formatDayOfYear
import com.etiennelawlor.eggday.ui.theme.EggDayDarkColors
import com.etiennelawlor.eggday.ui.theme.EggDayLightColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class EggDayWidget : GlanceAppWidget() {

    // Exact, so LocalSize is the real widget size and the egg can be sized to fit it.
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // A running session doesn't recompose on update(); reading this state makes
            // refreshAll() redraw it. Read the date in this same scope so it's re-evaluated.
            currentState(REFRESHED_AT)
            val today = LocalDate.now()
            // The egg keeps its own colors; the wide layout's card uses wallpaper-based
            // colors on Android 12+ and the egg-carton palette before that.
            val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                GlanceTheme.colors
            } else {
                ColorProviders(light = EggDayLightColors, dark = EggDayDarkColors)
            }
            GlanceTheme(colors = colors) {
                EggDayWidgetContent(today)
            }
        }
    }

    companion object {
        private val REFRESHED_AT = longPreferencesKey("refreshed_at")

        /** Redraws every Egg Day widget with today's date. Returns false when none are placed. */
        suspend fun refreshAll(context: Context): Boolean {
            val ids = GlanceAppWidgetManager(context).getGlanceIds(EggDayWidget::class.java)
            ids.forEach { id ->
                updateAppWidgetState(context, id) { it[REFRESHED_AT] = System.currentTimeMillis() }
            }
            EggDayWidget().updateAll(context)
            return ids.isNotEmpty()
        }
    }
}

@Composable
private fun EggDayWidgetContent(today: LocalDate) {
    val size = LocalSize.current
    val wide = size.width >= 160.dp && size.width >= size.height * 1.4f
    val openApp = actionStartActivity<MainActivity>()

    if (wide) {
        val eggHeight = min(size.height - 12.dp, 96.dp)
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.widgetBackground)
                .clickable(openApp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StampedEgg(today.dayOfYear, eggHeight)
            Spacer(GlanceModifier.width(12.dp))
            Column {
                Text(
                    text = LocalContext.current.getString(R.string.widget_label),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onSurfaceVariant,
                    ),
                )
                Text(
                    text = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    style = TextStyle(fontSize = 15.sp, color = GlanceTheme.colors.onSurface),
                )
            }
        }
    } else {
        // Just the egg, sitting on the wallpaper.
        Box(
            modifier = GlanceModifier.fillMaxSize().appWidgetBackground().clickable(openApp),
            contentAlignment = Alignment.Center,
        ) {
            StampedEgg(today.dayOfYear, min(size.height, size.width / EggArt.ASPECT) * 0.94f)
        }
    }
}

/** The egg drawable with the day number laid over it in dot-matrix digits. */
@Composable
private fun StampedEgg(day: Int, height: Dp) {
    val digitHeight = height * EggArt.DIGIT_HEIGHT
    val description = LocalContext.current.getString(R.string.egg_description, formatDayOfYear(day))
    Box(
        modifier = GlanceModifier.size(height * EggArt.ASPECT, height),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(R.drawable.egg),
            contentDescription = description,
            modifier = GlanceModifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        // Top padding of twice the offset moves the row's center down by the offset.
        Row(modifier = GlanceModifier.padding(top = height * EggArt.STAMP_OFFSET * 2)) {
            formatDayOfYear(day).forEachIndexed { index, digit ->
                if (index > 0) Spacer(GlanceModifier.width(digitHeight * EggArt.DIGIT_GAP))
                Image(
                    provider = ImageProvider(EggArt.DIGITS[digit - '0']),
                    contentDescription = null,
                    modifier = GlanceModifier.size(digitHeight * EggArt.DIGIT_ASPECT, digitHeight),
                )
            }
        }
    }
}
