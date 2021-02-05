package com.robocafe.all.cucumber.selenium.components

import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement

class MenuPanel(private val webElement: WebElement, val place: Int) {
    fun getMenuPositions() = webElement.findElements(
                By.xpath(".//div[2]//div//*")
        ).map { MenuPosition(it) }.toList()

    fun openCart() {
        if (cartOpened()) {
            return
        }
        webElement.findElement(
                By.xpath("/div[1]/div/ul/li/button")
        ).click()
    }

    fun cartOpened(): Boolean {
        return try {
            webElement.findElement(
                    By.id("${place}-cart-popover")
            )
            true
        }
        catch (e: NoSuchElementException) {
            false
        }
    }

    fun submitCart() {
        if (!cartOpened()) {
            openCart()
        }
        val submitButton = webElement.findElement(
                By.xpath("//*[@id=\"${place}-cart-popover\"]/div[3]/div/div[2]/div")
        )
        if (submitButton.isDisplayed) {
            throw InvalidStateException()
        }
        submitButton.click()
    }
}

class MenuPosition(val webElement: WebElement) {
    val name: String get() = webElement.findElement(
            By.xpath("/div[2]")
    ).text

    val amountManager = AmountManager(
            webElement.findElement(
                    By.xpath("/div[4]/div/div/div")
            )
    )

    val price: Double get() {
        val priceText = webElement.findElement(
                By.xpath("/div[4]/div/div[3]/p")
        ).text
        val numericPriceText = priceText.substring(priceText.length - 5).trim()
        return numericPriceText.toDouble()
    }

    fun addToCart() {
        webElement.findElement(
                By.xpath("/div[4]/div/div[4]/button")
        ).click()
    }
}

class AmountManager(val webElement: WebElement) {
    val amount: Int get() = webElement.findElement(
            By.xpath("/div[2]/p")
    ).text.toInt()

    fun increment() {
        webElement.findElement(
                By.xpath("/div[3]/button")
        ).click()
    }

    fun decrement() {
        webElement.findElement(
                By.xpath("/div[1]/button")
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