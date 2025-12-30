import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0"
    id("fabric-loom") version "1.14-SNAPSHOT"
    kotlin("plugin.serialization") version "2.0.20"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
}

repositories {
    mavenCentral()
    maven {
        name = "noxcrew-public"
        url = uri("https://maven.noxcrew.com/public")
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
    maven("https://maven.terraformersmc.com/") {
        name = "Terraformers"
    }
    maven("https://maven.enginehub.org/repo/") {
        name = "EngineHub"
    }
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    include(modImplementation("com.noxcrew.sheeplib:api:1.4.7+1.21.10")!!)
    modImplementation("dev.isxander:yet-another-config-lib:${project.property("yacl_version")}")
    modImplementation("com.terraformersmc:modmenu:${project.property("modmenu_version")}")
    modCompileOnly("com.noxcrew.noxesium:fabric:3.0.0-rc.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0-RC.2")
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.2")
}

@Suppress("UnstableApiUsage")
loom {
    mixin {
        defaultRefmapName.set("trident.refmap.json")
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version as Any,
            "minecraft_version" to project.property("minecraft_version") as Any,
            "loader_version" to project.property("loader_version") as Any,
            "kotlin_loader_version" to project.property("kotlin_loader_version") as Any
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.property("maven_group") as String
            artifactId = project.property("archives_base_name") as String
            version = version
            from(components["java"])
        }
    }

    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
