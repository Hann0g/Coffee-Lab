package com.example.data

import kotlinx.coroutines.flow.Flow

class CoffeeRepository(private val coffeeDao: CoffeeDao) {

    val allBrews: Flow<List<CoffeeBrew>> = coffeeDao.getAllBrews()
    val allRecipes: Flow<List<CoffeeRecipe>> = coffeeDao.getAllRecipes()
    val allBeanBags: Flow<List<BeanBag>> = coffeeDao.getAllBeanBags()
    val totalBrewsCount: Flow<Int> = coffeeDao.getTotalBrewsCount()
    val totalGramsUsed: Flow<Double?> = coffeeDao.getTotalGramsUsed()

    fun getBrewsSince(sinceTimestamp: Long): Flow<List<CoffeeBrew>> =
        coffeeDao.getBrewsSince(sinceTimestamp)

    suspend fun logBrew(brew: CoffeeBrew): Long {
        val brewId = coffeeDao.insertBrew(brew)
        // If associated with a bean bag, deduct the grams from the bag
        brew.bagId?.let { bagId ->
            coffeeDao.deductBeanWeight(bagId, brew.coffeeGrams)
        } ?: run {
            // Check if there is an active bean bag to deduct from
            val activeBag = coffeeDao.getActiveBeanBag()
            if (activeBag != null) {
                coffeeDao.deductBeanWeight(activeBag.id, brew.coffeeGrams)
            }
        }
        return brewId
    }

    suspend fun deleteBrew(id: Long) {
        coffeeDao.deleteBrewById(id)
    }

    suspend fun saveRecipe(recipe: CoffeeRecipe): Long {
        return if (recipe.id == 0L) {
            coffeeDao.insertRecipe(recipe)
        } else {
            coffeeDao.updateRecipe(recipe)
            recipe.id
        }
    }

    suspend fun deleteRecipe(id: Long) {
        coffeeDao.deleteRecipeById(id)
    }

    suspend fun saveBeanBag(bag: BeanBag): Long {
        return if (bag.id == 0L) {
            coffeeDao.insertBeanBag(bag)
        } else {
            coffeeDao.updateBeanBag(bag)
            bag.id
        }
    }

    suspend fun updateBeanBagWeight(bagId: Long, newWeight: Double) {
        coffeeDao.updateBeanBagWeight(bagId, newWeight)
    }

    suspend fun setActiveBeanBag(bagId: Long) {
        coffeeDao.setActiveBeanBag(bagId)
    }

    suspend fun deleteBeanBag(id: Long) {
        coffeeDao.deleteBeanBagById(id)
    }

    suspend fun getActiveBeanBag(): BeanBag? = coffeeDao.getActiveBeanBag()
}
