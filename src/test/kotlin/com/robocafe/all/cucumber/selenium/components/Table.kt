package com.robocafe.all.cucumber.selenium.components

import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver

enum class TableActiveWindow {
    RegistrationForm,
    StateScreen,
    MainPanels,
    Undefined
}

class InvalidStateException: RuntimeException()
class MainPanelNotFound: RuntimeException()

class Table(val webDriver: WebDriver, val tableNum: Int) {
    var selectedPerson: Int? = null
    init {
        val frontendServer = System.getProperty("frontendServer", "http://localhost:8080/static/index.html")
        webDriver.get(frontendServer)
    }

    val activeWindowType: TableActiveWindow get() {
        try {
            webDriver.findElement(
                    By.id("tableRegistrationForm")
            )
            return TableActiveWindow.RegistrationForm
        }
        catch (e: NoSuchElementException) {}
        try {
            webDriver.findElement(
                    By.id("coloredStateScreen")
            )
            return TableActiveWindow.StateScreen
        }
        catch (e: NoSuchElementException) {}
        try {
            webDriver.findElement(
                    By.id("1-person-panel")
            )
            return TableActiveWindow.MainPanels
        }
        catch (e: NoSuchElementException) {}
        return TableActiveWindow.Undefined
    }

    val registrationForm: RegistrationForm get() =
        if (activeWindowType == TableActiveWindow.RegistrationForm)
            RegistrationForm(webDriver)
        else throw InvalidStateException()

    val stateScreen: StateScreen get() =
        if (activeWindowType == TableActiveWindow.StateScreen)
            StateScreen(webDriver)
        else throw InvalidStateException()

    fun getMainPanelFor(personNum: Int): MainPanel {
        if (activeWindowType != TableActiveWindow.MainPanels) {
            throw InvalidStateException()
        }
        try {
            val panelElement = webDriver.findElement(
                    By.id("$personNum-person-panel")
            )
            return MainPanel(panelElement, personNum)
        }
        catch (e: NoSuchElementException) {
            throw MainPanelNotFound()
        }

    }
}