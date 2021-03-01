package com.robocafe.all.cucumber.selenium.components

import org.openqa.selenium.*
import org.openqa.selenium.support.ui.WebDriverWait

fun popoverOpened(id: String, context: SearchContext): Boolean {
    return try {
        context.findElement(
                By.id(id)
        )
        true
    }
    catch (e: NoSuchElementException) {
        false
    }
}

fun openPopover(
        id: String,
        buttonPath: String,
        context: SearchContext,
        webDriver: WebDriver
) {
    if (popoverOpened(id, context)) {
        return
    }
    context.findElement(
            By.xpath(buttonPath)
    ).click()

    WebDriverWait(webDriver, 10).until {
        popoverOpened(id, context)
    }
}

fun closePopover(id: String, context: SearchContext, webDriver: WebDriver) {
    if (!popoverOpened(id, context)) {
        return
    }

    WebDriverWait(webDriver, 10)
            .ignoring(StaleElementReferenceException::class.java)
            .until {
        val backdrop = context.findElement(
                By.id("$id-backdrop")
        )
        val executor = webDriver as JavascriptExecutor
        executor.executeScript("arguments[0].click();", backdrop)
        backdrop
    }

    WebDriverWait(webDriver, 10).until {
        !popoverOpened(id, context)
    }
}