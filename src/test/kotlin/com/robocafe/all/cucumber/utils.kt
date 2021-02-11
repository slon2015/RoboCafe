package com.robocafe.all.cucumber

import java.lang.Exception
import kotlin.reflect.KClass

fun readMoneyAmount(text: String, currencyPostfix: String = "Руб."): Double {
    val numericText = text.substring(0, text.length - currencyPostfix.length).trim()
    return numericText.toDouble()
}

class TimeoutException: RuntimeException()

fun <T> wait(supplier: () -> T, vararg exceptionsToIgnore: KClass<*>): T {
    var waitCount = 0
    val maxWaits = 5
    while (waitCount < maxWaits)
    {
        waitCount++
        try {
            return supplier()
        } catch (e: Throwable) {
            if (!exceptionsToIgnore.any { it.isInstance(e) }) {
                throw e
            } else {
                Thread.sleep(100)
            }
        }
    }
    throw TimeoutException()
}