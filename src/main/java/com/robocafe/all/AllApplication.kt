package com.robocafe.all

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories("com.robocafe")
@EntityScan(basePackages = ["com.robocafe.*"])
@ComponentScan("com.robocafe")
class AllApplication

fun main(args: Array<String>) {
    runApplication<AllApplication>(*args)
}