package com.robocafe.all.cucumber.selenium.components

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.WebDriverWait

enum class TableState(val color: String) {
    FREE("rgba(0, 128, 0, 1)"),
    WAIT_CLEARING("rgba(255, 255, 0, 1)")
}

class StateScreen(private val webDriver: WebDriver) {
    val status: TableState get() {
        val statePanel = webDriver.findElement(
                By.xpath("//*[@id=\"root\"]/div")
        )
        return when (statePanel.getCssValue("background-color")) {
            TableState.FREE.color -> TableState.FREE
            TableState.WAIT_CLEARING.color -> TableState.WAIT_CLEARING
            else -> throw InvalidStateException()
        }
    }

    val occupyButton: WebElement get() = webDriver.findElement(
            By.xpath("//*[@id=\"root\"]/div/button")
    )

    val optionsPopup: WebElement get() = webDriver.findElement(
            By.xpath("/html/body/div[2]/div[3]/div/div[2]/form/div/div/div")
    )

    fun getOption(persons: Int) = webDriver.findElement(
            By.xpath("//*[@id=\"menu-\"]/div[3]/ul//*[@data-value=\"${persons}\"]")
    )

    val okButton: WebElement get() = webDriver.findElement(
            By.xpath("/html/body/div[2]/div[3]/div/div[3]/button[2]")
    )

    fun occupyTable(persons: Int) {
        if (status != TableState.FREE) {
            throw InvalidStateException()
        }
        occupyButton.click()
        optionsPopup.click()
        WebDriverWait(webDriver, 10).until {
            val option = getOption(persons)
            option.isEnabled
        }
        val option = getOption(persons)
        val executor = webDriver as JavascriptExecutor
        executor.executeScript("arguments[0].click();", option);
        okButton.click()
    }
}