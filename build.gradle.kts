plugins {
    kotlin("jvm") version "1.3.61"
}

group = "eu.perfect.core"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        name = "okkero"
        url = uri("http://nexus.okkero.com/repository/maven-releases/")
    }
    maven {
        name = "spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

val spigot_version = "1.8.8-R0.1-SNAPSHOT"
val exposed_version = "0.21.1"
val hikari_version = "3.4.2"
val mongo_version = "3.11.0"
val redis_version = "3.0.1"
val skedule_version = "1.2.6"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc:spigot-api:$spigot_version")

    compile("org.jetbrains.exposed:exposed-core:$exposed_version")
    compile("org.jetbrains.exposed:exposed-dao:$exposed_version")
    compile("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    compile("com.zaxxer:HikariCP:$hikari_version")

    compile("org.mongodb:mongodb-driver:$mongo_version")
    compile("redis.clients:jedis:$redis_version")

    compile("com.okkero.skedule:skedule:$skedule_version")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
