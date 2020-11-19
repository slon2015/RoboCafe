package com.robocafe.all

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "positions")
data class PositionsConfiguration(
        var endpoints: Set<String>?
)

@Configuration
@ConfigurationProperties(prefix = "afiches")
data class AfichesConfiguration(
        var endpoints: Set<String>?
)

@Configuration
@ConfigurationProperties(prefix = "application")
class ApplicationConfiguration(
        var positions: PositionsConfiguration,
        var afiches: AfichesConfiguration
)