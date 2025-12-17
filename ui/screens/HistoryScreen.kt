package com.example.weeklyalcoholtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import com.example.weeklyalcoholtracker.repo.DrinkRepository
import com.example.weeklyalcoholtracker.util.WeekUtils
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, repo: DrinkRepository) {
    val context = LocalContext.current
    val prefsStore = remember { PrefsDataStore(context) }
    val prefs by prefsStore.prefsFlow.collectAsState(initial = null)

    val allEntries by repo.observeAll().collectAsState(initial = emptyList())

    var tab by remember { mutableStateOf(0) } // 0=this week, 1=all time

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        val p = prefs ?: return@Scaffold

        val zone = ZoneId.systemDefault()
        val weekKeyNow = WeekUtils.weekStartKey(
            System.currentTimeMillis(),
            p.weekStartDay,
            p.weekStartMinuteOfDay,
            zone
        )

        val thisWeek = remember(allEntries, weekKeyNow) {
            allEntries.filter { it.weekKey == weekKeyNow }
        }

        val allTimeGrouped = remember(allEntries) {
            allEntries
                .sortedByDescending { it.timestamp }
                .groupBy { it.weekKey } // weekKey is YYYY-MM-DD of start date
                .toSortedMap(compareByDescending { it })
        }

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("This week") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("All time") })
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (tab == 0) {
                    if (thisWeek.isEmpty()) {
                        item { Text("No drinks logged this week.") }
                    } else {
                        items(thisWeek) { e ->
                            HistoryRow(
                                title = e.drinkName,
                                subtitle = "${e.volumeMl.toInt()} ml • ${e.abvPercent}%",
                                trailing = e.timestamp.toString()
                            )
                        }
                    }
                } else {
                    if (allTimeGrouped.isEmpty()) {
                        item { Text("No drinks logged yet.") }
                    } else {
                        allTimeGrouped.forEach { (weekKey, entries) ->
                            item {
                                Text(
                                    text = "Week starting $weekKey",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(6.dp))
                                Divider()
                                Spacer(Modifier.height(6.dp))
                            }
                            items(entries) { e ->
                                HistoryRow(
                                    title = e.drinkName,
                                    subtitle = "${e.volumeMl.toInt()} ml • ${e.abvPercent}%",
                                    trailing = e.timestamp.toString()
                                )
                            }
                            item { Spacer(Modifier.height(12.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(title: String, subtitle: String, trailing: String) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(trailing, style = MaterialTheme.typography.bodySmall)
        }
    }
}