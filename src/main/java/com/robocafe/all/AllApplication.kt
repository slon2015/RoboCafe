package com.robocafe.all

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AllApplication

fun main(args: Array<String>) {
    runApplication<AllApplication>(*args)
}