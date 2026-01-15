plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("com.github.jk1.dependency-license-report") version "3.0.1"
    `java-test-fixtures`
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "kotlin")
    apply(plugin = "java-test-fixtures")

    group = "com.zalphion.featurecontrol"
    version = "latest-SNAPSHOT"

    dependencies {
        api(platform("org.http4k:http4k-bom:_"))
        api(platform("dev.forkhandles:forkhandles-bom:_"))
        api(platform("org.testcontainers:testcontainers-bom:_"))
        api(platform("org.junit:junit-bom:_"))
        api(platform("io.kotest:kotest-bom:_"))

        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testFixturesApi("org.junit.jupiter:junit-jupiter-api")
        testFixturesApi("io.kotest:kotest-assertions-core-jvm")

    }

    tasks.test {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(25)
    }

    tasks.compileKotlin {
        compilerOptions {
            allWarningsAsErrors = true
        }
    }
}

licenseReport {
    allowedLicensesFile = File("$rootDir/allowed-licenses.json")
}

tasks.check {
    dependsOn(tasks.checkLicense)
}