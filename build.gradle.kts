plugins {
    kotlin("jvm") version "1.9.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0-RC")
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}