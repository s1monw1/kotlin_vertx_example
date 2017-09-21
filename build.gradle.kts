import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.*

val kotlin_version = "1.1.4-3"
val vertx_version = "3.4.2"

plugins {
    application
    kotlin("jvm")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "de.swirtz.vertx.standalone.webserver.StarterKt"
    applicationName = "kotlinwithvertx"
    version = "1.0-SNAPSHOT"
    group = "example"
}


dependencies {
    compile(kotlin("stdlib", kotlin_version))
    compile(kotlin("reflect", kotlin_version))

    compile("io.vertx:vertx-core:$vertx_version")
    compile("io.vertx:vertx-web:$vertx_version")
    compile("io.vertx:vertx-web-templ-thymeleaf:$vertx_version")
    compile("org.slf4j:slf4j-api:1.7.14")
    compile("ch.qos.logback:logback-classic:1.1.3")

    testCompile(kotlin("test-junit", kotlin_version))
    testCompile("junit:junit:4.11")
    testCompile("io.vertx:vertx-unit:$vertx_version")
}

repositories {
    mavenCentral()
    jcenter()

}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = application.applicationName
    manifest {
        attributes["Main-Class"] = application.mainClassName
    }
    from(configurations.runtime.map {
        if (it.isDirectory) it else zipTree(it)
    })
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
