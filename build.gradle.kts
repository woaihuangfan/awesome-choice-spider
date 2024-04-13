import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
}

group = "com.fan"
version = "0.0.1-SNAPSHOT"


val hutoolVersion = "5.8.26"
val gsonVersion = "2.8.8"
val poiVersion = "5.2.5"
val coroutinesVersion = "1.7.2"
java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("cn.hutool:hutool-all:$hutoolVersion")
    implementation("org.apache.derby:derby")
    implementation("org.apache.derby:derbyclient")
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("com.google.code.gson:gson")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")


    implementation("org.apache.derby:derby")
    implementation("org.apache.derby:derbytools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
