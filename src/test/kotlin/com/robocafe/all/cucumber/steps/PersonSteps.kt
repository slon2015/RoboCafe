package com.robocafe.all.cucumber.steps

import com.robocafe.all.cucumber.SpringIntegrationTest
import io.cucumber.java.en.Then
import io.cucumber.spring.ScenarioScope
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Assert.*


@ScenarioScope
class PersonSteps @Autowired constructor(val tablesSteps: TablesSteps): SpringIntegrationTest() {

    @Then("Balance of person {int} on table {int} equals {double}")
    fun checkBalance(personNum: Int, tableNum: Int, expectedBalance: Double) {
        val actualBalance = tablesSteps.tables[tableNum]!!.getMainPanelFor(personNum).balance
        assertEquals(expectedBalance, actualBalance, 0.0)
    }
}