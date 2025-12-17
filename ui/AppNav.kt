package com.example.weeklyalcoholtracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weeklyalcoholtracker.repo.DrinkRepository
import com.example.weeklyalcoholtracker.ui.screens.HistoryScreen
import com.example.weeklyalcoholtracker.ui.screens.HomeScreen
import com.example.weeklyalcoholtracker.ui.screens.SettingsScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val HISTORY = "history"
}

@Composable
fun AppNav(navController: NavHostController, repo: DrinkRepository) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController, repo = repo)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController, repo = repo)
        }
    }
}