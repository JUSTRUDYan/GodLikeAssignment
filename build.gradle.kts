plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "org.lucya"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.13.2")

}

tasks {
    runServer {
        minecraftVersion("1.20.4")
    }
    shadowJar {
        mergeServiceFiles()
    }
}
kotlin {
    jvmToolchain(17)
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor( 0, "seconds")
}