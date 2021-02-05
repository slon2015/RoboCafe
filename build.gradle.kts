import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    kotlin("plugin.jpa") version "1.4.10"
}

group = "com.robocafe.all"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("io.springfox:springfox-swagger-ui:3.0.0")
    implementation("org.flywaydb:flyway-core")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    testRuntimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.cucumber:cucumber-java:6.8.1")
    testImplementation("io.cucumber:cucumber-spring:6.8.1")
    testImplementation("io.cucumber:cucumber-junit:6.8.1")
    testImplementation("org.seleniumhq.selenium:selenium-java")
    testImplementation("org.seleniumhq.selenium:selenium-chrome-driver")
}

configurations.create("cucumberRuntime").extendsFrom(configurations.testImplementation.get(),
        configurations.testRuntimeOnly.get())

task<JavaExec>("cucumber") {
    val frontendServer: Boolean = System.getProperty("devServer") != null
    dependsOn(
            tasks.findByPath("assemble"),
            tasks.findByPath("testClasses")
    )
    if (!frontendServer) {
        dependsOn(tasks.findByPath(":RoboCafeFront:npm_run_build"))
    }
    group = "verification"
    main = "io.cucumber.core.cli.Main"
    classpath(configurations["cucumberRuntime"],
            sourceSets.main.get().output,
            sourceSets.test.get().output)
    args("--plugin", "pretty", "--glue", "com.robocafe.all.cucumber", "src/test/resources")
    if (!frontendServer) {
        systemProperty("spring.resources.static-locations", "RoboCafeFront/build")
    }
    else {
        systemProperty("frontendServer", "http://localhost:3000")
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xinline-classes")
        jvmTarget = "1.8"
    }
}

//tasks.getByName("build") {
//    dependsOn(project(":RoboCafeFront").tasks.findByName("npm_run_build"))
//}

tasks.withType<BootJar> {
    exclude("application-dev*.yml")
    exclude("db/devMigrations")
    from("RoboCafeFront/build") {
        into("static")
    }
}

val imageVersion = System.getenv("IMAGE_VERSION") ?: "dev"

tasks.withType<BootBuildImage> {
    imageName = "robocafe/all:${imageVersion}"
}



