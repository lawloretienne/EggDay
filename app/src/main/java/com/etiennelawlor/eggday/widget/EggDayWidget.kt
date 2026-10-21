package com.etiennelawlor.eggday.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.currentState
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.etiennelawlor.eggday.MainActivity
import com.etiennelawlor.eggday.R
import com.etiennelawlor.eggday.formatDayOfYear
import com.etiennelawlor.eggday.ui.theme.EggDayDarkColors
import com.etiennelawlor.eggday.ui.theme.EggDayLightColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class EggDayWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // A running session doesn't recompose on update(); reading this state makes
            // refreshAll() redraw it. Read the date in this same scope so it's re-evaluated.
            currentState(REFRESHED_AT)
            val today = LocalDate.now()

            // Wallpaper-based colors on Android 12+, the egg-carton palette before that.
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
    val context = LocalContext.current
    val size = LocalSize.current
    val showDate = size.height >= 100.dp && size.width >= 110.dp
    val numberSize = when {
        size.height < 60.dp || size.width < 80.dp -> 26.sp
        showDate -> 52.sp
        else -> 36.sp
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.primaryContainer)
            .clickable(actionStartActivity<MainActivity>())
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = formatDayOfYear(today.dayOfYear),
            style = TextStyle(
                fontSize = numberSize,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onPrimaryContainer,
            ),
        )
        Text(
            text = context.getString(R.string.widget_label),
            style = TextStyle(fontSize = 11.sp, color = GlanceTheme.colors.onPrimaryContainer),
        )
        if (showDate) {
            Text(
                text = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onPrimaryContainer),
                modifier = GlanceModifier.padding(top = 4.dp),
            )
        }
    }
}
