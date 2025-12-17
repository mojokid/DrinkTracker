package com.example.weeklyalcoholtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import com.example.weeklyalcoholtracker.repo.DrinkRepository
import com.example.weeklyalcoholtracker.ui.Routes
import com.example.weeklyalcoholtracker.ui.components.BeerGlassProgress
import com.example.weeklyalcoholtracker.util.WeekUtils
import java.time.ZoneId
import kotlin.math.roundToInt

private const val ML_PER_OZ = 29.5735295625
private const val SERVING_BASE_OZ = 1.5
private const val SERVING_BASE_ABV = 40.0
private const val SERVING_BASE_ML = SERVING_BASE_OZ * ML_PER_OZ

private fun servingsFrom(volumeMl: Double, abvPercent: Double): Double {
    val pureAlcoholEquivalent = volumeMl * (abvPercent / 100.0)
    val basePureAlcohol = SERVING_BASE_ML * (SERVING_BASE_ABV / 100.0)
    return if (basePureAlcohol <= 0) 0.0 else pureAlcoholEquivalent / basePureAlcohol
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, repo: DrinkRepository) {
    val context = LocalContext.current
    val prefsStore = remember { PrefsDataStore(context) }
    val prefs by prefsStore.prefsFlow.collectAsState(initial = null)

    val allEntries by repo.observeAll().collectAsState(initial = emptyList())
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Alcohol Tracker") },
                actions = {
                    TextButton(onClick = { navController.navigate(Routes.HISTORY) }) { Text("History") }
                    TextButton(onClick = { navController.navigate(Routes.SETTINGS) }) { Text("Settings") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Text("+") }
        }
    ) { padding ->
        val p = prefs ?: return@Scaffold

        val weekKeyNow = WeekUtils.weekStartKey(
            nowMs = System.currentTimeMillis(),
            weekStartIsoDay = p.weekStartDay,
            weekStartMinuteOfDay = p.weekStartMinuteOfDay,
            zoneId = ZoneId.systemDefault()
        )

        val thisWeekEntries = remember(allEntries, weekKeyNow) {
            allEntries.filter { it.weekKey == weekKeyNow }
        }

        val recentDrinkNames = remember(allEntries) {
            allEntries
                .sortedByDescending { it.timestamp }
                .map { it.drinkName }
                .distinct()
                .take(12)
        }

        val weeklyLimit = p.weeklyServingsLimit
        val consumedServings = remember(thisWeekEntries) {
            thisWeekEntries.sumOf { e -> servingsFrom(e.volumeMl, e.abvPercent) }
        }

        val remaining = (weeklyLimit - consumedServings).coerceAtLeast(0.0)
        val fraction = if (weeklyLimit <= 0) 0f else (consumedServings / weeklyLimit).coerceIn(0.0, 1.0).toFloat()
        val percent = (fraction * 100f).roundToInt()

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BeerGlassProgress(
                fraction = fraction,
                height = 260.dp
            )

            Text(
                text = "${consumedServings.roundToInt()}/${weeklyLimit.roundToInt()} servings ($percent%)",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Remaining: ${"%.2f".format(remaining)} servings",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (showAdd) {
            AddDrinkDialog(
                repo = repo,
                prefsStore = prefsStore,
                prefs = p,
                recentDrinkNames = recentDrinkNames,
                onDismiss = { showAdd = false }
            )
        }
    }
}