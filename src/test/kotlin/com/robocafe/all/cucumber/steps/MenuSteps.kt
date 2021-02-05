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
        menuPositionRepository.save(
                Position(
                        UUID.randomUUID().toString(),
                        name,
                        mutableSetOf(Category.FOOD),
                        cost,
                        null
                )
        )
    }
}