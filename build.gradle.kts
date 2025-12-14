// This buildscript block is for defining the versions of the build plugins.
// It does NOT define repositories for the whole project, which was the error.
buildscript {    repositories {
    google()
    mavenCentral()
}
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.gms:google-services:4.4.1")
    }
}