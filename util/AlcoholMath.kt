package com.example.weeklyalcoholtracker.util

/**
 * Serving definition:
 * 1.5 oz of 40% alcohol.
 *
 * In pure alcohol terms:
 * pureAlcoholOzPerServing = 1.5 * 0.40 = 0.6 fl oz ethanol (approx)
 *
 * We compute servings by:
 * servings = (amountOz * (abv/100)) / 0.6
 * or in ml:
 * servings = (amountMl * (abv/100)) / (0.6 oz in ml)
 */
object AlcoholMath {
    private const val OUNCES_PER_SERVING_BASE = 1.5
    private const val BASE_ABV = 0.40
    private const val PURE_ALCOHOL_OZ_PER_SERVING = OUNCES_PER_SERVING_BASE * BASE_ABV // 0.6
    private const val ML_PER_FL_OZ = 29.5735295625
    private const val PURE_ALCOHOL_ML_PER_SERVING = PURE_ALCOHOL_OZ_PER_SERVING * ML_PER_FL_OZ

    fun servingsFromMl(amountMl: Double, abvPercent: Double): Double {
        val fraction = abvPercent / 100.0
        val pureAlcoholMl = amountMl * fraction
        return pureAlcoholMl / PURE_ALCOHOL_ML_PER_SERVING
    }

    fun mlFromOz(oz: Double): Double = oz * ML_PER_FL_OZ
    fun ozFromMl(ml: Double): Double = ml / ML_PER_FL_OZ
}