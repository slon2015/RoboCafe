package com.robocafe.all.cucumber.selenium.components

import com.robocafe.all.cucumber.readMoneyAmount
import org.openqa.selenium.By
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
}