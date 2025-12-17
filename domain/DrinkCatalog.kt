package com.example.weeklyalcoholtracker.domain

data class DrinkItem(
    val name: String,
    val abvPercent: Double
)

object DrinkCatalog {

    val allDrinks: List<DrinkItem> = listOf(

        // --- Beer ---
        DrinkItem("Beer – Lager", 5.0),
        DrinkItem("Beer – IPA", 6.5),
        DrinkItem("Beer – Stout", 6.0),
        DrinkItem("Beer – Porter", 5.5),
        DrinkItem("Beer – Pilsner", 5.0),
        DrinkItem("Beer – Wheat", 5.2),
        DrinkItem("Beer – Sour", 5.0),
        DrinkItem("Beer – Belgian Tripel", 9.0),
        DrinkItem("Beer – Barleywine", 10.0),

        // --- Wine ---
        DrinkItem("Wine – Red", 13.5),
        DrinkItem("Wine – White", 12.5),
        DrinkItem("Wine – Rosé", 12.0),
        DrinkItem("Sparkling Wine", 12.0),
        DrinkItem("Champagne", 12.0),
        DrinkItem("Port", 20.0),
        DrinkItem("Sherry", 17.0),

        // --- Spirits ---
        DrinkItem("Vodka", 40.0),
        DrinkItem("Gin", 40.0),
        DrinkItem("Rum", 40.0),
        DrinkItem("Tequila", 40.0),
        DrinkItem("Whiskey", 40.0),
        DrinkItem("Bourbon", 45.0),
        DrinkItem("Scotch", 43.0),
        DrinkItem("Brandy", 40.0),
        DrinkItem("Cognac", 40.0),
        DrinkItem("Absinthe", 55.0),

        // --- Liqueurs ---
        DrinkItem("Amaro", 28.0),
        DrinkItem("Aperol", 11.0),
        DrinkItem("Campari", 24.0),
        DrinkItem("Triple Sec", 30.0),
        DrinkItem("Cointreau", 40.0),
        DrinkItem("Baileys", 17.0),
        DrinkItem("Jägermeister", 35.0),

        // --- Cocktails (avg ABV) ---
        DrinkItem("Margarita", 20.0),
        DrinkItem("Martini", 28.0),
        DrinkItem("Old Fashioned", 28.0),
        DrinkItem("Negroni", 24.0),
        DrinkItem("Mojito", 14.0),
        DrinkItem("Gin & Tonic", 10.0),
        DrinkItem("Whiskey Sour", 16.0),

        // --- Other ---
        DrinkItem("Hard Seltzer", 5.0),
        DrinkItem("Cider", 5.0),
        DrinkItem("Mead", 10.0),
        DrinkItem("Sake", 15.0)
    )

    fun findByName(name: String): DrinkItem? =
        allDrinks.firstOrNull { it.name.equals(name, ignoreCase = true) }
}