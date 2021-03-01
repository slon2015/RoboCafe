package com.robocafe.all.cucumber.steps

import com.robocafe.all.cucumber.selenium.components.Table
import com.robocafe.all.cucumber.selenium.components.TableActiveWindow
import com.robocafe.all.cucumber.SpringIntegrationTest
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.spring.ScenarioScope
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait


class TableNotSelected: RuntimeException()

@ScenarioScope
class TablesSteps: SpringIntegrationTest() {
    val tables: MutableMap<Int, Table> = mutableMapOf()
    var selectedTable: Table? = null
    get() {
        if (field == null) {
            throw TableNotSelected()
        }
        return field
    }

    @Given("Registered table with number {int}")
    fun registerTable(tableNumber: Int) {
        val options = ChromeOptions()
        options.addArguments("--incognito", "--start-maximized")
        val webDriver: WebDriver = ChromeDriver(options)
        val table = Table(webDriver, tableNumber)
        table.registrationForm.registerTable(tableNumber)
        tables[tableNumber] = table
        selectTable(tableNumber)
        WebDriverWait(webDriver, 10).until {
            table.activeWindowType == TableActiveWindow.StateScreen
        }
    }

    @When("Table app reloaded")
    fun reloadTableApp() {
        val table = selectedTable!!
        val oldWindowType = table.activeWindowType
        table.webDriver.navigate().refresh()
        WebDriverWait(table.webDriver, 10).until {
            table.activeWindowType == oldWindowType
        }
    }

    private fun selectTable(tableNumber: Int) {
        selectedTable = tables[tableNumber]
    }

    fun close() {
        tables.values.forEach { it.webDriver.quit() }
        tables.clear()
    }
}