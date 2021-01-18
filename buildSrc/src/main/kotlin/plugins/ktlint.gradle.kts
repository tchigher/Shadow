//repositories {
//    jcenter()
//}
//configurations {
//    ktlint
//}
//dependencies {
//    ktlint "com.pinterest:ktlint:0.34.2"
//}
//task ktlint(type: JavaExec, group: "verification") {
//    description = "Check Kotlin code style."
//    classpath = configurations.ktlint
//    main = "com.pinterest.ktlint.Main"
//    args "src/**/*.kt"
//}
//task ktlintFormat(type: JavaExec, group: "formatting") {
//    description = "Fix Kotlin code style deviations."
//    classpath = configurations.ktlint
//    main = "com.pinterest.ktlint.Main"
//    args "-F", "src/**/*.kt"
//}

package plugins

val ktlint : Configuration by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.34.2")
}

tasks {
    // checks project to make sure all kt files obey kotlin standards
    register<JavaExec>("ktlint") {
        group = "verification"
        description = "Check Koltin code style."
        classpath = ktlint
        main = "com.pinterest.ktlint.Main"
        args("src/**/*.kt")
        // args("--android", "src/**/*.kt")
    }

    // fixes problems found in the ktlint task
    // -F stands for auto fix violations
    register<JavaExec>("ktlintFormat") {
        group = "formatting"
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        main = "com.pinterest.ktlint.Main"
        // args("--android", "-F", "src/**/*.kt")
        args("-F", "src/**/*.kt")
    }
}
