package com.example

import com.example.data.AiRecipeAssistantService
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAiRecipeAssistant_PourOverFloral() {
    val rec = AiRecipeAssistantService.generateFallbackRecommendation(
      beanName = "Ethiopia Yirgacheffe",
      intendedUse = "Bright floral pour over"
    )
    assertEquals("Pour Over", rec.method)
    assertTrue("Should recommend light roast for floral Ethiopian", rec.roastLevel == "Light")
    assertEquals(250.0, rec.waterGrams, 0.1)
    assertEquals(94, rec.targetTempCelsius)
    assertEquals(3, rec.numberOfPours)
    assertTrue("Should include bloom and pours", rec.stepsText.contains("Bloom") && rec.stepsText.contains("Pour 2"))
  }

  @Test
  fun testAiRecipeAssistant_Espresso() {
    val rec = AiRecipeAssistantService.generateFallbackRecommendation(
      beanName = "Colombia Huila",
      intendedUse = "Rich morning espresso for latte"
    )
    assertEquals("Espresso", rec.method)
    assertEquals(18.0, rec.coffeeGrams, 0.1)
    assertEquals(36.0, rec.waterGrams, 0.1)
    assertTrue("Should recommend fine grind for espresso", rec.grindSize.contains("Fine"))
  }

  @Test
  fun testAiRecipeAssistant_AeroPress() {
    val rec = AiRecipeAssistantService.generateFallbackRecommendation(
      beanName = "Guatemala Antigua",
      intendedUse = "Smooth AeroPress for afternoon"
    )
    assertEquals("AeroPress", rec.method)
    assertEquals(225.0, rec.waterGrams, 0.1)
    assertEquals(88, rec.targetTempCelsius)
    assertEquals(2, rec.numberOfPours)
  }
}
