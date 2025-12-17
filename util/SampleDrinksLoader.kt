package com.example.weeklyalcoholtracker.util

import android.content.Context
import org.json.JSONArray

object SampleDrinksLoader {

    fun loadFromAssets(context: Context): List<DrinkDefinition> {
        val json = context.assets.open("drinks.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        val out = ArrayList<DrinkDefinition>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(
                DrinkDefinition(
                    name = o.getString("name"),
                    abv = o.getDouble("abv")
                )
            )
        }
        return out
    }

    fun popularDefaults(): List<DrinkDefinition> = listOf(
        DrinkDefinition("Beer", 5.0),
        DrinkDefinition("Wine", 13.0),
        DrinkDefinition("Vodka", 40.0),
        DrinkDefinition("Whiskey", 40.0),
        DrinkDefinition("Gin", 40.0),
        DrinkDefinition("Rum", 40.0)
    )
}