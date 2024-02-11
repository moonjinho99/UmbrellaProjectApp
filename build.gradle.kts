buildscript {
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.2")
        classpath ("com.github.dcendents:android-maven-gradle-plugin:2.1")

    }
}

plugins {
    id("com.android.application") version "8.2.1" apply false
}

