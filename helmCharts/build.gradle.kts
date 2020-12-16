plugins {
    id("org.unbroken-dome.helm") version "1.4.0"
}

group = "com.robocafe.all"

helm {
    charts {
        create("all") {
            chartName.set("rcall")
            chartVersion.set(parent!!.version.toString())
            sourceDir.set(file("src/main/all"))
        }
    }
}

tasks.getByName("helmPackageAllChart") {
    dependsOn(":bootBuildImage")
}

