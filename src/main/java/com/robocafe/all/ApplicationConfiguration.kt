package com.robocafe.all

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Configuration

class PositionsConfiguration @ConstructorBinding constructor(
        val endpoints: Set<String>
)

class AfichesConfiguration @ConstructorBinding constructor(
        val endpoints: Set<String>
)

@Configuration
@ConfigurationProperties(prefix = "application")
class ApplicationConfiguration {
        @NestedConfigurationProperty
        lateinit var positions: PositionsConfiguration
        @NestedConfigurationProperty
        lateinit var afiches: AfichesConfiguration
}