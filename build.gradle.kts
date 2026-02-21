import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.kotlin.serialization)
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
    maven(url = "https://jitpack.io")
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

    // Fabric
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(libs.fabric.api)

    // Deps
    modImplementation(libs.sheeplib.api)
    include(libs.sheeplib.api)
    modImplementation(libs.yacl)
    modImplementation(libs.modmenu)
    modCompileOnly(libs.noxesium.fabric)
    implementation(libs.kpresence)
    include(libs.kpresence)
    modRuntimeOnly(libs.dev.auth.fabric) // development-only

    // Kotlin
    implementation(libs.kotlinx.serialization.json)
    compileOnlyApi(libs.kotlinx.coroutines.core)
}

loom {
    accessWidenerPath = file("src/main/resources/trident.accesswidener")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", libs.versions.fabric.loader.get())
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version")!!,
            "loader_version" to libs.versions.fabric.loader.get(),
            "kotlin_loader_version" to libs.versions.fabric.kotlin.loader.get()
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

tasks.remapJar {
    archiveFileName = "${project.property("archives_base_name")}-${version}+${project.property("minecraft_version")}.jar"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.property("maven_group") as String
            artifactId = project.property("archives_base_name") as String
            version = version
            from(components["java"])
        }
    }
}
