package com.robocafe.all.cucumber.selenium.components

import com.robocafe.all.cucumber.readMoneyAmount
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class MenuPanel(private val webElement: WebElement, val place: Int) {
    fun getMenuPositions() = webElement.findElements(
                By.xpath(".//div[2]/div/div")
        ).map { MenuPosition(it) }.toList()


    fun openCart(webDriver: WebDriver) =
            openPopover(
                    "$place-cart-popover",
                    ".//div[1]/div/ul/li/button",
                    webElement,
                    webDriver
            )

    fun closeCart(webDriver: WebDriver) =
            closePopover("$place-cart-popover", webElement, webDriver)

    fun cartOpened() = popoverOpened("$place-cart-popover", webElement)

    fun getCartPositions(parentDriver: WebDriver): List<CartPosition> {
        val cartWasOpened = cartOpened()
        if (!cartWasOpened) {
            openCart(parentDriver)
        }
        val positions = webElement.findElements(
                By.xpath("//*[@id=\"$place-cart-popover\"]/div/div[1]/div/ul/li")
        ).map { CartPosition(it) }.toList()
        if (!cartWasOpened) {
            closeCart(parentDriver)
        }
        return positions
    }

    val submitButton: WebElement get() = webElement.findElement(
            By.xpath("//*[@id=\"$place-cart-popover\"]/div/div/div[2]/div/button")
    )

    fun submitCart() {
        if (!submitButton.isEnabled || !submitButton.isDisplayed) {
            throw InvalidStateException()
        }
        submitButton.click()
    }
}

class CartPosition(val webElement: WebElement) {
    val name: String get() = webElement.findElement(
            By.xpath(".//div/div[1]/p")
    ).text

    val amountManager = AmountManager(
            webElement.findElement(
                    By.xpath(".//div[3]/div")
            )
    )

    val deleteButton: WebElement get() = webElement.findElement(
            By.xpath(".//div[4]/button")
    )
}

class MenuPosition(val webElement: WebElement) {
    val name: String get() = webElement.findElement(
            By.xpath(".//div[2]")
    ).text

    val amountManager = AmountManager(
            webElement.findElement(
                    By.xpath(".//div[4]/div/div/div")
            )
    )

    val price: Double get() {
        val priceText = webElement.findElement(
                By.xpath(".//div[4]/div/div[3]/p")
        ).text
        return readMoneyAmount(priceText)
    }

    fun addToCart() {
        webElement.findElement(
                By.xpath(".//div[4]/div/div[4]/button")
        ).click()
    }
}

class AmountManager(val webElement: WebElement) {
    val amount: Int get() = webElement.findElement(
            By.xpath(".//div[2]/p")
    ).text.toInt()

    fun increment() {
        webElement.findElement(
                By.xpath(".//div[3]/button")
        ).click()
    }

    fun decrement() {
        webElement.findElement(
                By.xpath(".//div[1]/button")
        ).click()
    }

    fun setAmount(expectedAmount: Int) {
        if (expectedAmount <= 0) {
            return
        }
        var currentAmount = amount
        while (currentAmount != expectedAmount) {
            if (currentAmount > expectedAmount) {
                decrement()
                currentAmount -= 1
            }
            else {
                increment()
                currentAmount += 1
            }
        }
    }
}