package com.robocafe.all.cucumber.selenium.components

import com.robocafe.all.cucumber.readMoneyAmount
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class MainPanel(private val webElement: WebElement, val place: Int) {

    val balance: Double get() = readMoneyAmount(
            webElement.findElement(
                    By.xpath(".//div/div/div/ul/li/div/span")
            ).text
    )

    fun openMenuPanel(): MenuPanel {
        val menuButton = webElement.findElement(
                By.xpath(".//div/div/div/ul/div[2]")
        )
        menuButton.click()
        val menuSpace = webElement.findElement(
                By.xpath(".//main/div")
        )
        return MenuPanel(menuSpace, place)
    }

    fun openOrdersPanel(webDriver: WebDriver) =
            openPopover(
                    "$place-order-popover",
                    ".//div[1]/button",
                    webElement,
                    webDriver
            )

    fun closeOrdersPanel(webDriver: WebDriver) =
            closePopover("$place-order-popover", webElement, webDriver)

    fun ordersPanelOpened() = popoverOpened("$place-order-popover", webElement)

    fun getOrderPositions(parentDriver: WebDriver): List<OrderPosition> {
        val panelWasOpened = ordersPanelOpened()
        if (!panelWasOpened) {
            openOrdersPanel(parentDriver)
        }
        val positions = webElement.findElements(
                By.xpath("//*[@id=\"$place-order-popover\"]/div/ul/li")
        ).map { OrderPosition(it) }.toList()
        if (!panelWasOpened) {
            closeOrdersPanel(parentDriver)
        }
        return positions
    }
}

class OrderPosition(val webElement: WebElement) {
    private val positionText: String get() = webElement.findElement(
            By.xpath(".//div/span")
    ).text
    val name: String get() =
        positionText.substring(0, positionText.indexOf(':')).trim()
    val status: String get() =
        positionText.substring(positionText.indexOf(": ") + 2).trim()
    val cancelButton: WebElement? get() {
        return try {
            webElement.findElement(
                    By.xpath(".//button")
            )
        }
        catch (err: NoSuchElementException) {
            null
        }
    }

}