package com.robocafe.all.cucumber

import com.robocafe.all.cucumber.steps.TablesSteps
import io.cucumber.java.After
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils

@RunWith(Cucumber::class)
@CucumberOptions
class CucumberGlue @Autowired constructor(val tablesSteps: TablesSteps,
                                          val jdbcTemplate: JdbcTemplate) {

    @After
    fun cleanUpDb() {
        JdbcTestUtils.deleteFromTables(
                jdbcTemplate,
                "cafe_table",
                "person",
                "party",
                "order_position",
                "menu_position_categories",
                "menu_position",
                "menu_order",
                "message",
                "chat_member",
                "chat"
        )
    }

    @After
    fun cleanUpTables() {
        tablesSteps.close()
    }
}