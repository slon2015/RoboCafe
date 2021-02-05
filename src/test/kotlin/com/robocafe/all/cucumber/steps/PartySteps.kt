package com.robocafe.all.cucumber.steps

import com.robocafe.all.cucumber.selenium.components.TableActiveWindow
import com.robocafe.all.cucumber.SpringIntegrationTest
import io.cucumber.java.en.When
import io.cucumber.spring.ScenarioScope
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired

@ScenarioScope
class PartySteps @Autowired constructor(val tablesSteps: TablesSteps): SpringIntegrationTest() {
    private fun startPartyHelper(persons: Int) {
        tablesSteps.selectedTable!!.stateScreen.occupyTable(persons)
        WebDriverWait(tablesSteps.selectedTable!!.webDriver, 10).until {
            tablesSteps.selectedTable!!.activeWindowType == TableActiveWindow.MainPanels
        }
    }

    @When("Started party for {int} persons")
    fun startPartyForPersons(persons: Int) = startPartyHelper(persons)

    @When("Started party for single person")
    fun startPartyForSinglePerson() {
        startPartyHelper(1)
        tablesSteps.selectedTable!!.selectedPerson = 1
    }
}