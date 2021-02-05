package com.robocafe.all.cucumber.selenium.components

import io.cucumber.java.PendingException
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class MainPanel(private val webElement: WebElement, val place: Int) {

    val balance: Double get() {
        throw PendingException()
    }

    fun openMenuPanel(): MenuPanel {
        val menuButton = webElement.findElement(
                By.xpath("/div/div/div/ul/div[2]")
        )
        menuButton.click()
        val menuSpace = webElement.findElement(
                By.xpath("/main/div")
        )
        return MenuPanel(menuSpace, place)
    }
}