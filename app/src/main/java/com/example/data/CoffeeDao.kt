package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {
    // --- Brews ---
    @Query("SELECT * FROM coffee_brews ORDER BY timestamp DESC")
    fun getAllBrews(): Flow<List<CoffeeBrew>>

    @Query("SELECT * FROM coffee_brews WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getBrewsSince(sinceTimestamp: Long): Flow<List<CoffeeBrew>>

    @Query("SELECT * FROM coffee_brews WHERE id = :id")
    suspend fun getBrewById(id: Long): CoffeeBrew?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrew(brew: CoffeeBrew): Long

    @Update
    suspend fun updateBrew(brew: CoffeeBrew)

    @Delete
    suspend fun deleteBrew(brew: CoffeeBrew)

    @Query("DELETE FROM coffee_brews WHERE id = :id")
    suspend fun deleteBrewById(id: Long)

    @Query("SELECT COUNT(*) FROM coffee_brews")
    fun getTotalBrewsCount(): Flow<Int>

    @Query("SELECT SUM(coffeeGrams) FROM coffee_brews")
    fun getTotalGramsUsed(): Flow<Double?>

    // --- Recipes ---
    @Query("SELECT * FROM coffee_recipes ORDER BY isPreset DESC, name ASC")
    fun getAllRecipes(): Flow<List<CoffeeRecipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: CoffeeRecipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<CoffeeRecipe>)

    @Update
    suspend fun updateRecipe(recipe: CoffeeRecipe)

    @Query("DELETE FROM coffee_recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: Long)

    @Query("SELECT COUNT(*) FROM coffee_recipes")
    suspend fun getRecipeCount(): Int

    // --- Bean Bags (Inventory) ---
    @Query("SELECT * FROM bean_bags ORDER BY isActive DESC, id DESC")
    fun getAllBeanBags(): Flow<List<BeanBag>>

    @Query("SELECT * FROM bean_bags WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveBeanBag(): BeanBag?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeanBag(bag: BeanBag): Long

    @Update
    suspend fun updateBeanBag(bag: BeanBag)

    @Query("DELETE FROM bean_bags WHERE id = :id")
    suspend fun deleteBeanBagById(id: Long)

    @Query("UPDATE bean_bags SET remainingWeightGrams = :weight WHERE id = :bagId")
    suspend fun updateBeanBagWeight(bagId: Long, weight: Double)

    @Query("UPDATE bean_bags SET isActive = CASE WHEN id = :bagId THEN 1 ELSE 0 END")
    suspend fun setActiveBeanBag(bagId: Long)

    @Query("UPDATE bean_bags SET remainingWeightGrams = MAX(0.0, remainingWeightGrams - :gramsUsed) WHERE id = :bagId")
    suspend fun deductBeanWeight(bagId: Long, gramsUsed: Double)
}
