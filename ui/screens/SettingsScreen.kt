package com.example.weeklyalcoholtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import com.example.weeklyalcoholtracker.workers.WorkScheduler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val store = remember { PrefsDataStore(context) }
    val prefs by store.prefsFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        val p = prefs ?: return@Scaffold

        var weekStartDay by remember { mutableStateOf(p.weekStartDay) }
        var startHourText by remember { mutableStateOf((p.weekStartMinuteOfDay / 60).toString()) }
        var startMinText by remember { mutableStateOf((p.weekStartMinuteOfDay % 60).toString().padStart(2, '0')) }

        var weeklyLimitText by remember { mutableStateOf(p.weeklyServingsLimit.toString()) }
        var useMetric by remember { mutableStateOf(p.useMetric) }
        var carryOver by remember { mutableStateOf(p.carryOverEnabled) }
        var penalty by remember { mutableStateOf(p.carryOverPenaltyPercent) }
        var notifications by remember { mutableStateOf(p.notificationsEnabled) }

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text("When the week starts", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            val days = listOf(
                1 to "Mon",
                2 to "Tue",
                3 to "Wed",
                4 to "Thu",
                5 to "Fri",
                6 to "Sat",
                7 to "Sun"
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = days.first { it.first == weekStartDay }.second,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Week start day") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    days.forEach { (v, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { weekStartDay = v; expanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Week starts at", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startHourText,
                    onValueChange = { startHourText = it },
                    label = { Text("Hour (0-23)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = startMinText,
                    onValueChange = { startMinText = it },
                    label = { Text("Minute (0-59)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = weeklyLimitText,
                onValueChange = { weeklyLimitText = it },
                label = { Text("Servings per week") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Units: Metric (ml)")
                Switch(checked = useMetric, onCheckedChange = { useMetric = it })
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Carry-over")
                Switch(checked = carryOver, onCheckedChange = { carryOver = it })
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = penalty.toString(),
                onValueChange = { penalty = it.toIntOrNull() ?: penalty },
                enabled = carryOver,
                label = { Text("Carry-over penalty % (default 50)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Notifications")
                Switch(checked = notifications, onCheckedChange = { notifications = it })
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        val lim = weeklyLimitText.toDoubleOrNull() ?: 7.0

                        val h = startHourText.toIntOrNull()?.coerceIn(0, 23) ?: 2
                        val m = startMinText.toIntOrNull()?.coerceIn(0, 59) ?: 0
                        val startMinutes = h * 60 + m

                        store.update {
                            it.copy(
                                weekStartDay = weekStartDay,
                                weekStartMinuteOfDay = startMinutes,
                                weeklyServingsLimit = lim,
                                useMetric = useMetric,
                                carryOverEnabled = carryOver,
                                carryOverPenaltyPercent = penalty.coerceIn(0, 100),
                                notificationsEnabled = notifications
                            )
                        }

                        // reschedule based on new day+time
                        WorkScheduler.scheduleWeeklyReset(context)

                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}