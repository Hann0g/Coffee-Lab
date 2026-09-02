package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CoffeeBrew::class, CoffeeRecipe::class, BeanBag::class],
    version = 2,
    exportSchema = false
)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao

    companion object {
        @Volatile
        private var INSTANCE: CoffeeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CoffeeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoffeeDatabase::class.java,
                    "coffee_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(CoffeeDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class CoffeeDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.coffeeDao())
                }
            }
        }

        suspend fun populateInitialData(dao: CoffeeDao) {
            val presetRecipes = listOf(
                CoffeeRecipe(
                    name = "V60 Pour Over",
                    method = "Pour Over",
                    coffeeOrigin = "Ethiopia Yirgacheffe",
                    roastLevel = "Light",
                    tastingNotes = "Jasmine, Lemon Bergamot, Peach",
                    defaultCoffeeGrams = 15.0,
                    defaultWaterGrams = 250.0,
                    ratio = "1:16.7",
                    grindSize = "Medium-Fine",
                    targetTimeSeconds = 210,
                    targetTempCelsius = 93,
                    steps = "1. Bloom with 45g water for 45s.\n2. Pour gently up to 150g in 30s.\n3. Pour to 250g by 1:45.\n4. Gentle stir and let drawdown complete around 3:30.",
                    isPreset = true
                ),
                CoffeeRecipe(
                    name = "Double Espresso",
                    method = "Espresso",
                    coffeeOrigin = "Guatemala Antigua",
                    roastLevel = "Medium-Dark",
                    tastingNotes = "Dark Chocolate, Roasted Hazelnut, Caramel",
                    defaultCoffeeGrams = 18.0,
                    defaultWaterGrams = 36.0,
                    ratio = "1:2.0",
                    grindSize = "Fine",
                    targetTimeSeconds = 28,
                    targetTempCelsius = 94,
                    steps = "1. Distribute and tamp firmly.\n2. Target 36g liquid yield in 27–30s.\n3. Look for rich hazelnut crema with tiger stripes.",
                    isPreset = true
                ),
                CoffeeRecipe(
                    name = "AeroPress (Standard)",
                    method = "AeroPress",
                    coffeeOrigin = "Colombia Huila",
                    roastLevel = "Medium",
                    tastingNotes = "Red Apple, Brown Sugar, Milk Chocolate",
                    defaultCoffeeGrams = 12.0,
                    defaultWaterGrams = 200.0,
                    ratio = "1:16.6",
                    grindSize = "Medium-Fine",
                    targetTimeSeconds = 120,
                    targetTempCelsius = 90,
                    steps = "1. Add coffee and 200g water in 10s.\n2. Stir 3 times, insert plunger to seal.\n3. At 1:30, gently press down for 30s until hiss.",
                    isPreset = true
                ),
                CoffeeRecipe(
                    name = "French Press (Hoffmann)",
                    method = "Immersion",
                    coffeeOrigin = "Sumatra Mandheling",
                    roastLevel = "Dark",
                    tastingNotes = "Cedar, Cacao Nibs, Earthy Spice",
                    defaultCoffeeGrams = 30.0,
                    defaultWaterGrams = 500.0,
                    ratio = "1:16.7",
                    grindSize = "Medium-Coarse",
                    targetTimeSeconds = 480,
                    targetTempCelsius = 95,
                    steps = "1. Pour 500g boiling water over coffee.\n2. Wait 4 minutes undisturbed.\n3. Stir surface crust to sink grounds.\n4. Scoop foam, wait 4 more mins, then pour gently without plunging down.",
                    isPreset = true
                ),
                CoffeeRecipe(
                    name = "Chemex 3-Cup",
                    method = "Pour Over",
                    coffeeOrigin = "Kenya Nyeri",
                    roastLevel = "Light-Medium",
                    tastingNotes = "Blackcurrant, Grapefruit, Honey",
                    defaultCoffeeGrams = 25.0,
                    defaultWaterGrams = 400.0,
                    ratio = "1:16.0",
                    grindSize = "Medium-Coarse",
                    targetTimeSeconds = 240,
                    targetTempCelsius = 94,
                    steps = "1. Rinse thick filter thoroughly.\n2. 70g bloom for 45s.\n3. Continuous spiral pours keeping water level steady.",
                    isPreset = true
                ),
                CoffeeRecipe(
                    name = "Cold Brew Concentrate",
                    method = "Cold Brew",
                    coffeeOrigin = "Brazil Santos",
                    roastLevel = "Medium",
                    tastingNotes = "Toasted Almond, Chocolate Fudge, Vanilla",
                    defaultCoffeeGrams = 80.0,
                    defaultWaterGrams = 480.0,
                    ratio = "1:6.0",
                    grindSize = "Coarse",
                    targetTimeSeconds = 43200, // 12 hours
                    targetTempCelsius = 20,
                    steps = "1. Combine coarse coffee with filtered room temp water.\n2. Steep in fridge or cool room for 12–16 hours.\n3. Filter twice and dilute 1:1 with water or milk.",
                    isPreset = true
                )
            )
            dao.insertRecipes(presetRecipes)

            // Starter bean bag
            val starterBag = BeanBag(
                name = "Ethiopia Yirgacheffe Natural",
                roaster = "Artisan Roast",
                roastLevel = "Light",
                roastDate = "Aug 24, 2026",
                initialWeightGrams = 250.0,
                remainingWeightGrams = 250.0,
                isActive = true
            )
            dao.insertBeanBag(starterBag)
        }
    }
}
