package com.robocafe.all.cucumber.steps

import com.robocafe.all.menu.Category
import com.robocafe.all.menu.Position
import com.robocafe.all.menu.PositionRepository
import io.cucumber.java.en.Given
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class MenuSteps @Autowired constructor(
        private val menuPositionRepository: PositionRepository
) {

    @Given("Created food type with name {string} and cost {double}")
    fun createFoodType(name: String, cost: Double) {
        createMenuPosition(name, cost, Category.FOOD)
    }

    @Given("Created food type with name {string}")
    fun createFoodTypeWithoutPrice(name: String) {
        createMenuPosition(name, 100.0, Category.FOOD)
    }

    private fun createMenuPosition(name: String, cost: Double, category: Category) {
        menuPositionRepository.save(
                Position(
                        UUID.randomUUID().toString(),
                        name,
                        mutableSetOf(category),
                        cost,
                        null
                )
        )
    }
}