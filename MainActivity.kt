package com.example.weeklyalcoholtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.weeklyalcoholtracker.db.AppDatabase
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import com.example.weeklyalcoholtracker.repo.DrinkRepository
import com.example.weeklyalcoholtracker.ui.AppNav
import com.example.weeklyalcoholtracker.ui.theme.WeeklyAlcoholTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.get(this)
        val dao = db.drinkDao()
        val prefs = PrefsDataStore(this)
        val repo = DrinkRepository(dao = dao, prefs = prefs)

        setContent {
            WeeklyAlcoholTrackerTheme {
                val navController = rememberNavController()
                AppNav(navController = navController, repo = repo)
            }
        }
    }
}