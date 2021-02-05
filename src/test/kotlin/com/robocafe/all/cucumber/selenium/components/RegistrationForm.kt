package com.robocafe.all.cucumber.selenium.components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class RegistrationForm(private val webDriver: WebDriver) {
    val keyboard = Keyboard(webDriver.findElement(
            By.xpath("/html/body/div[2]/div[3]/div/div/div")
    ))
    val form: WebElement get() = webDriver.findElement(
            By.xpath("/html/body/div[2]/div[3]/div/div/form")
    )
    val coreUrlField: WebElement get() = form.findElement(
            By.xpath("//*[@id=\"core-url\"]")
    )
    val tableNumField: WebElement get() = form.findElement(
            By.xpath("//*[@id=\"table-num\"]")
    )
    val maxPersonsField: WebElement get() = form.findElement(
            By.xpath("//*[@id=\"table-max-persons\"]")
    )
    val passwordField: WebElement get() = form.findElement(
            By.xpath("//*[@id=\"admin-password\"]")
    )
    val registerButton: WebElement get() = webDriver.findElement(
            By.xpath("/html/body/div[2]/div[3]/div/header/div/button")
    )

    fun registerTable(tableNum: Int) {
        tableNumField.click()
        keyboard.backspace()
        keyboard.typeText(tableNum.toString())
        passwordField.click()
        keyboard.typeText("admin")
        registerButton.click()
    }
}