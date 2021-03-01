package com.robocafe.all.cucumber.steps

import com.robocafe.all.application.services.OrderInfo
import com.robocafe.all.application.services.OrderPositionInfo
import com.robocafe.all.cucumber.selenium.components.CartPosition
import com.robocafe.all.cucumber.selenium.components.OrderPosition
import com.robocafe.all.cucumber.selenium.components.Table
import com.robocafe.all.cucumber.wait
import com.robocafe.all.domain.OrderStatus
import com.robocafe.all.menu.PositionService
import com.robocafe.all.session.SessionService
import io.cucumber.java.PendingException
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException

class OrderSteps @Autowired constructor(
        val tablesSteps: TablesSteps,
        val sessionService: SessionService,
        val positionService: PositionService
) {

    data class OrderPositionData(val name: String, val amount: Int) {
        constructor(data: CartPosition): this(data.name, data.amountManager.amount)
    }

    private fun moveToCartHelper(table: Table, person: Int, foodName: String, amount: Int) {
        val normalizedFoodName = foodName.trim()
        val menu = table.getMainPanelFor(person).openMenuPanel()

        val prevPositionAmount = menu.getCartPositions(table.webDriver)
                .map { OrderPositionData(it) }
                .find { it.name.equals(normalizedFoodName, ignoreCase = true) }?.amount ?: 0

        val position = requireNotNull(menu.getMenuPositions().find {
            it.name.equals(normalizedFoodName, ignoreCase = true)
        })
        position.amountManager.setAmount(amount)
        position.addToCart()
        menu.openCart(table.webDriver)
        WebDriverWait(table.webDriver, 10).ignoring(NumberFormatException::class.java).until {
            val currentPositionAmount = menu.getCartPositions(table.webDriver)
                    .map { OrderPositionData(it) }
                    .find { it.name.equals(normalizedFoodName, ignoreCase = true) }?.amount ?: 0
            currentPositionAmount - amount == prevPositionAmount
        }
        menu.closeCart(table.webDriver)
    }

    private fun makeOrderHelper(table: Table, person: Int) {
        val menu = table.getMainPanelFor(person).openMenuPanel()
        menu.openCart(table.webDriver)
        menu.submitCart()
        menu.openCart(table.webDriver)
        WebDriverWait(table.webDriver, 10).until {
            menu.getCartPositions(table.webDriver)
                    .isEmpty()
        }
        menu.closeCart(table.webDriver)
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

    fun checkOrderPositionsHelper(positionsCount: Int, foodName: String, state: String) {
        val table = tablesSteps.selectedTable!!
        val mainPanel = table.getMainPanelFor(table.selectedPerson!!)
        mainPanel.openOrdersPanel(table.webDriver)
        lateinit var positions: List<OrderPosition>
        WebDriverWait(table.webDriver, 10).until {
            positions = mainPanel.getOrderPositions(table.webDriver)
            positions.isNotEmpty()
        }
        val actualCount = positions.filter {
            it.name == foodName && it.status == state
        }.size
        mainPanel.closeOrdersPanel(table.webDriver)
        Assertions.assertEquals(positionsCount, actualCount)
    }



    @Then("Orders panel contains {int} order position for {string} with scheduled status")
    fun checkOrderInPanelWaitingStatus(positionsCount: Int, foodName: String) {
        checkOrderPositionsHelper(positionsCount, foodName, "Ожидает приготовления")
    }

    @Then("Orders panel contains {int} order position for {string} with preparing status")
    fun checkOrderInPanelPreparingStatus(positionsCount: Int, foodName: String) {
        checkOrderPositionsHelper(positionsCount, foodName, "В процессе приготовления")
    }

    data class OrderAndPosition(
            val order: OrderInfo,
            val position: OrderPositionInfo
    ) {
        constructor(data: Pair<OrderPositionInfo, OrderInfo>): this(data.second, data.first)
    }

    private fun selectPositions(positionsCount: Int, foodName: String, status: OrderStatus): List<OrderAndPosition> {
        val table = sessionService.getTableByNumber(tablesSteps.selectedTable!!.tableNum)
        val person = sessionService.getActivePartyForTable(table.id).members.find {
            it.place == tablesSteps.selectedTable!!.selectedPerson
        }!!
        val positions = sessionService.getOpenOrdersForPerson(person.id).flatMap {
            it.positions.map { position -> position to it }
        }.map { it to positionService.getPositionInfo(it.first.menuPositionId).name }
                .filter { it.second == foodName &&
                        it.first.first.orderStatus == status }
                .subList(0, positionsCount).map { it.first }
        return positions.map { OrderAndPosition(it) }
    }

    @When("Changing status {int} of {string} on preparing")
    fun movePositionStatusToPreparing(positionsCount: Int, foodName: String) {
        val positions = wait(
                { selectPositions(positionsCount, foodName, OrderStatus.WAITING) },
                IndexOutOfBoundsException::class
        ) // Wait changes saving into db

        positions.forEach {
            sessionService.startPositionPreparing(it.order.id, it.position.id)
        }
    }

    @When("Changing status {int} of {string} on completed")
    fun movePositionStatusToCompleted(positionsCount: Int, foodName: String) {
        val positions = wait(
                { selectPositions(positionsCount, foodName, OrderStatus.WAITING) },
                IndexOutOfBoundsException::class
        )

        positions.forEach {
            sessionService.startPositionPreparing(it.order.id, it.position.id)
            sessionService.finishPositionPreparing(it.order.id, it.position.id)
            sessionService.finishPositionDelivering(it.order.id, it.position.id)
        }

        Thread.sleep(100)
    }
}