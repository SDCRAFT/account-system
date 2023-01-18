import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("war")
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "org.sdcraft.web"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
    }
    maven {
        setUrl("https://maven.aliyun.com/repository/spring/")
    }
    mavenLocal()
    mavenCentral()
}

dependencies {
    //spring
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:3.0.1")

    implementation("org.apache.logging.log4j:log4j-iostreams:2.19.0")

    implementation("org.apache.ibatis:ibatis-core:3.0")
    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")


}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Add-Opens"] = "java.base/jdk.internal.loader"
        attributes["Add-Exports"] = "java.base/jdk.internal.loader"
    }
}
configurations.all {
    exclude ("org.springframework.boot","spring-boot-starter-logging")
}

