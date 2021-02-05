package com.robocafe.all.cucumber.selenium.components

import com.robocafe.all.cucumber.readMoneyAmount
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait

class MenuPanel(private val webElement: WebElement, val place: Int) {
    fun getMenuPositions() = webElement.findElements(
                By.xpath(".//div[2]/div/div")
        ).map { MenuPosition(it) }.toList()

    fun openCart() {
        if (cartOpened()) {
            return
        }
        webElement.findElement(
                By.xpath(".//div[1]/div/ul/li/button")
        ).click()
    }

    fun closeCart() {
        if (!cartOpened()) {
            return
        }
        webElement.findElement(
                By.xpath("//*[@id=\"$place-cart-popover\"]/div[1]")
        ).click()
    }

    fun cartOpened(): Boolean {
        return try {
            webElement.findElement(
                    By.xpath("//*[@id=\"$place-cart-popover\"]")
            )
            true
        }
        catch (e: NoSuchElementException) {
            false
        }
    }

    fun getOrderPositions(parentDriver: WebDriver): List<OrderPosition> {
        openCart()
        WebDriverWait(parentDriver, 10).until {
            cartOpened()
        }
        val positions = webElement.findElements(
                By.xpath("//*[@id=\"$place-cart-popover\"]/div[3]/div/div[1]/ul/li")
        ).map { OrderPosition(it) }.toList()
        closeCart()
        WebDriverWait(parentDriver, 10).until {
            !cartOpened()
        }
        return positions
    }

    val submitButton: WebElement get() = webElement.findElement(
            By.xpath("//*[@id=\"$place-cart-popover\"]/div[3]/div/div[2]/div/button")
    )

    fun submitCart() {
        if (!submitButton.isEnabled || !submitButton.isDisplayed) {
            throw InvalidStateException()
        }
        submitButton.click()
    }
}

class OrderPosition(val webElement: WebElement) {
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

    fun setAmount(amount: Int) {
        if (amount <= 0) {
            return
        }
        var currentAmount = amount
        while (currentAmount != amount) {
            if (currentAmount > amount) {
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