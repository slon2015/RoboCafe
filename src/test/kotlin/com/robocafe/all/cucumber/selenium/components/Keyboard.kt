package com.robocafe.all.cucumber.selenium.components

import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement

enum class LayoutType {
    RU, ENG, NUM
}

class Keyboard(private val webElement: WebElement) {
    private fun getKey(key: String) = webElement.findElement(
            By.xpath("//*[@data-skbtn=\"${key}\"]")
    )

    fun getGlob(): LayoutType {
        return try {
            val globKey = getKey("{glob}")
            when (globKey.findElement(By.xpath("/span")).text) {
                "язык" -> LayoutType.RU
                else -> LayoutType.ENG
            }
        }
        catch (e: NoSuchElementException) {
            LayoutType.NUM
        }
    }

    private fun changeGlob() {
        getKey("{glob}").click()
    }

    private fun typeCharacter(character: Char) {
        val button = try {
            getKey(character.toString())
        }
        catch (e: NoSuchElementException) {
            changeGlob()
            getKey(character.toString())
        }
        button.click()
    }

    fun backspace() {
        getKey("{bksp}").click()
    }

    fun typeText(text: String) {
        text.forEach(this::typeCharacter)
    }
}