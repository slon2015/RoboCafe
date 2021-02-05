package com.robocafe.all.cucumber.steps

import com.robocafe.all.cucumber.selenium.components.OrderPosition
import com.robocafe.all.cucumber.selenium.components.Table
import io.cucumber.java.en.When
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import java.lang.NumberFormatException

class OrderSteps @Autowired constructor(val tablesSteps: TablesSteps) {

    data class OrderPositionData(val name: String, val amount: Int) {
        constructor(data: OrderPosition): this(data.name, data.amountManager.amount)
    }

    private fun moveToCartHelper(table: Table, person: Int, foodName: String, amount: Int) {
        val normalizedFoodName = foodName.trim()
        val menu = table.getMainPanelFor(person).openMenuPanel()

        val prevPositionAmount = menu.getOrderPositions(table.webDriver)
                .map { OrderPositionData(it) }
                .find { it.name.equals(normalizedFoodName, ignoreCase = true) }?.amount ?: 0

        val position = requireNotNull(menu.getMenuPositions().find {
            it.name.equals(normalizedFoodName, ignoreCase = true)
        })
        position.amountManager.setAmount(amount)
        position.addToCart()
        WebDriverWait(table.webDriver, 10).ignoring(NumberFormatException::class.java).until {
            val currentPositionAmount = menu.getOrderPositions(table.webDriver)
                    .map { OrderPositionData(it) }
                    .find { it.name.equals(normalizedFoodName, ignoreCase = true) }?.amount ?: 0
            currentPositionAmount - amount == prevPositionAmount
        }
    }

    private fun makeOrderHelper(table: Table, person: Int) {
        val menu = table.getMainPanelFor(person).openMenuPanel()
        menu.openCart()
        WebDriverWait(table.webDriver, 10).until {
            menu.cartOpened()
        }
        menu.submitCart()
        WebDriverWait(table.webDriver, 10).until {
            menu.getOrderPositions(table.webDriver)
                    .isEmpty()
        }
    }

    @When("Move to cart {string} with amount {int}")
    fun moveToCart(foodName: String, amount: Int) {
        val table = requireNotNull(tablesSteps.selectedTable)
        moveToCartHelper(table, requireNotNull(table.selectedPerson), foodName, amount)
    }

    @When("Making order")
    fun makeOrder() {
        val table = requireNotNull(tablesSteps.selectedTable)
        makeOrderHelper(table, requireNotNull(table.selectedPerson))
    }
}