plugins {
    kotlin("jvm") version "1.8.21"

    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0" // Adds runServer and runMojangMappedServer tasks for testing

    //Shading & relocating into the plugin JAR
    id("com.github.johnrengelman.shadow") version "7.0.0"

    kotlin("plugin.serialization") version "1.9.0"
}

group = "net.topstrix"
version = "0.1.0"
description = "Hub Interactions"
val apiVersion = "1.20"


java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        // e.g this is how you would add jitpack
        maven { url = uri("https://jitpack.io") }
        // Add any repositories you would be adding to all projects here
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT") //the paper dev bundle is a compile-only dependency, paper itself provides it. No need to shade

    implementation("net.kyori:adventure-text-minimessage:4.14.0") //todo: shade
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") //todo: shade
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") //todo: shade
    implementation("com.charleskorn.kaml:kaml:0.55.0") //todo: shade
    implementation("com.zaxxer:HikariCP:5.0.1")

    compileOnly("com.github.gameoholic:partigon:1.0.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("me.clip:placeholderapi:2.11.4")
}




tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to apiVersion
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }


    shadowJar {
        // helper function to relocate a package into our package
        fun reloc(pkg: String) = relocate(pkg, "${project.group}.${project.name}.dependency.$pkg")

        reloc("com.github.gameoholic")
        reloc("kotlin")

    }

}


