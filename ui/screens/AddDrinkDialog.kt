package com.example.weeklyalcoholtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weeklyalcoholtracker.domain.DrinkCatalog
import com.example.weeklyalcoholtracker.domain.DrinkItem
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import com.example.weeklyalcoholtracker.prefs.UserPrefs
import com.example.weeklyalcoholtracker.repo.DrinkRepository
import kotlinx.coroutines.launch

private const val ML_PER_OZ = 29.5735295625

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDrinkDialog(
    repo: DrinkRepository,
    prefsStore: PrefsDataStore,
    prefs: UserPrefs,
    recentDrinkNames: List<String>,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val recent = recentDrinkNames.mapNotNull { DrinkCatalog.findByName(it) }
    val combined = (recent + DrinkCatalog.allDrinks).distinctBy { it.name }

    val lastName = prefs.lastDrinkName
    val lastAbv = prefs.lastDrinkAbvPercent
    val lastUnitMetric = prefs.lastUseMetric

    val initialItem: DrinkItem? = lastName?.let { DrinkCatalog.findByName(it) } ?: combined.firstOrNull()

    var query by remember { mutableStateOf(lastName ?: (initialItem?.name ?: "")) }
    var expanded by remember { mutableStateOf(true) }
    var selected by remember { mutableStateOf<DrinkItem?>(initialItem) }

    var abvText by remember {
        mutableStateOf(
            (lastAbv ?: selected?.abvPercent ?: 5.0).toString()
        )
    }
    var amountText by remember { mutableStateOf("") }
    var useMetricHere by remember { mutableStateOf(lastUnitMetric) }

    val filtered = remember(query, combined) {
        val q = query.trim()
        if (q.isBlank()) combined.take(30) else combined.filter { it.name.contains(q, ignoreCase = true) }.take(50)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add drink") },
        confirmButton = {
            Button(onClick = {
                val amount = amountText.toDoubleOrNull() ?: return@Button
                val abv = abvText.toDoubleOrNull() ?: return@Button
                if (abv <= 0 || abv > 100) return@Button

                val ml = if (useMetricHere) amount else amount * ML_PER_OZ
                val finalName = selected?.name ?: query.trim().ifBlank { "Custom drink" }

                scope.launch {
                    repo.addDrink(
                        drinkName = finalName,
                        abvPercent = abv,
                        volumeMl = ml
                    )

                    // Remember last drink + unit + ABV (including user edits)
                    prefsStore.update {
                        it.copy(
                            lastDrinkName = finalName,
                            lastDrinkAbvPercent = abv,
                            lastUseMetric = useMetricHere
                        )
                    }

                    onDismiss()
                }
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        text = {
            Column {

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        expanded = true
                    },
                    label = { Text("Search / drink name") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (expanded && filtered.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(filtered) { item ->
                            ListItem(
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text("${item.abvPercent}%") },
                                modifier = Modifier.clickable {
                                    selected = item
                                    query = item.name
                                    // set ABV to default for that drink; user can still edit it
                                    abvText = item.abvPercent.toString()
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = abvText,
                    onValueChange = { abvText = it },
                    label = { Text("ABV %") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !useMetricHere,
                        onClick = { useMetricHere = false },
                        label = { Text("oz") }
                    )
                    FilterChip(
                        selected = useMetricHere,
                        onClick = { useMetricHere = true },
                        label = { Text("ml") }
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (useMetricHere) "Amount (ml)" else "Amount (oz)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}