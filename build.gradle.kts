import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library") version "8.0.2"
    id("org.jetbrains.kotlin.android") version "1.7.20"
    id("maven-publish")
//    id("org.jetbrains.kotlin.android") version "1.7.20"
}

var renamedArtifactLocation=""

val publishProperties = Properties()
val file = File("publish.properties")
if (file.exists())
    FileInputStream(file).use { stream ->
        publishProperties.load(stream)
    }

val libName = "compose-form-state-utils"
val newVersion = publishProperties["VERSION"]

android {
    namespace = "com.mobily.composeformstateutils"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
//    libraryVariants.forEach { variant ->
//        variant.outputs.all {
//            val versionCode = newVersion
//            val versionName = "v-$newVersion"
//            val appName = libName // You can use a custom name if you prefer
//            val buildType = variant.buildType.name
//            val newAarName = "$appName-$versionCode-$buildType.aar"
//            renamedArtifactLocation="${outputFile.path}/${newAarName}"
//            println("renamed file:${renamedArtifactLocation}")
//          outputFile.renameTo(File(renamedArtifactLocation))
//        }
//    }
}

dependencies {
    implementation("androidx.compose.runtime:runtime:1.5.4")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.compose.foundation:foundation:1.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //To support property reflection (ClassName::class.members)
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}


fun groovy.util.Node.appendDependencyNode(dependency: Dependency, scope: String) {
    appendNode("dependency").apply {
        appendNode("groupId", dependency.group)
        appendNode("artifactId", dependency.name)
        appendNode("version", dependency.version)
        appendNode("scope", scope)
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.mobily"
                artifactId = "composeformstateutils"
                version = newVersion.toString()
                artifact("${buildDir}/outputs/aar/compose-form-state-utils-release.aar")
                pom.withXml {
                    // including api dependencies
                    val dependenciesNode = asNode().appendNode("dependencies")
                    // Including implementation dependencies
                    configurations["implementation"].allDependencies.forEach { dep ->
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", dep.group)
                        dependencyNode.appendNode("artifactId", dep.name)
                        dependencyNode.appendNode("version", dep.version)
                        dependencyNode.appendNode("scope", "compile")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/AnasEsh/ComposeFormStateUtils")
                credentials {
                    username = "AnasEsh"
                    password = "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"
                }
                isAllowInsecureProtocol = true
            }
        }
    }

//    publishing {
//        publications {
//            create<MavenPublication>("maven") {
//                groupId = "com.mobily" // Replace with your group ID
//                artifactId = "composeformstateutils" // Replace with your artifact ID
//                version = "1.0.0" // Replace with your version
//
//                // Specify the AAR artifact
////            from(components["debug"])
//
//                pom.withXml {
//                    // Create a new 'dependencies' node
//                    val dependenciesNode = asNode().appendNode("dependencies")
//                    // Iterate over all dependencies in the 'api' configuration
//                    configurations.getByName("api").allDependencies.forEach { dep ->
//                        val dependencyNode = dependenciesNode.appendNode("dependency")
//                        dependencyNode.appendNode("groupId", dep.group)
//                        dependencyNode.appendNode("artifactId", dep.name)
//                        dependencyNode.appendNode("version", dep.version)
//                        dependencyNode.appendNode("scope", "compile")
//                    }
//
//                    configurations.getByName("implementation").allDependencies.forEach { dep ->
//                        val dependencyNode = dependenciesNode.appendNode("dependency")
//                        dependencyNode.appendNode("groupId", dep.group)
//                        dependencyNode.appendNode("artifactId", dep.name)
//                        dependencyNode.appendNode("version", dep.version)
//                        dependencyNode.appendNode("scope", "compile")
//                    }
//                }
//            }
//        }
//        repositories {
//            maven {
//                url = uri("${rootProject.buildDir}/repo") // Publish to the local directory
//            }
//        }
//    }


//    publishing {
//
//        publications {
//            create<MavenPublication>("release") {
//                groupId = "com.mobily"
//                artifactId = "formstate"
//                version = "1.0.0"
//                println("Available components: ${components.names}")
//                from(components["release"])
//
////                artifact(tasks.getByName("bundleReleaseAar"))
////
////                // Include transitive dependencies in the POM file
////                pom {
////                    withXml {
////                        asNode().appendNode("dependencies").apply {
////                            configurations["api"].dependencies.forEach {
////                                appendDependencyNode(it, "compile")
////                            }
////                            configurations["implementation"].dependencies.forEach {
////                                appendDependencyNode(it, "runtime")
////                            }
////                        }
////                    }
////                }
//            }
//        }
//
//        repositories {
//            maven {
//                url = uri("${project.buildDir}/repo")
//            }
//        }
//    }
}