plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()

    google()
    jcenter()

    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    api("com.android.tools.build:gradle:4.0.2")

    implementation("com.tencent.bugly:symtabfileuploader:2.2.1") // 支持 JCenter 和 MavenCentral
    implementation("io.objectbox:objectbox-gradle-plugin:2.3.4")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
