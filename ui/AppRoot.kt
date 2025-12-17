package com.example.weeklyalcoholtracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.weeklyalcoholtracker.repo.DrinkRepository

@Composable
fun AppRoot(repo: DrinkRepository) {
    val navController = rememberNavController()
    AppNav(navController = navController, repo = repo)
}