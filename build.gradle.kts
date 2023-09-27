import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion = project.property("kotlin.version")
    val aliyunMavenUrl = project.property("aliyun.maven.url")
    repositories {
        maven { url = uri(aliyunMavenUrl!!) }
        mavenLocal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.3")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
    }
}
subprojects {
    val aliyunMavenUrl = project.property("aliyun.maven.url")
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    repositories {

        maven { url = uri(aliyunMavenUrl!!) }
        mavenLocal()
    }

    group = "cn.dongjak"
    version = "1.0"
    plugins.withId("io.spring.dependency-management") {
        configure<DependencyManagementExtension> {
            repositories {

                maven { url = uri(aliyunMavenUrl!!) }
                mavenLocal()
            }
            imports {
                mavenBom("cn.dongjak:dongjak-dependencies-manage:1.0")
            }
        }
    }

    plugins.withType<JavaPlugin>() {

        configure<JavaPluginExtension> {
            toolchain {
                sourceCompatibility = JavaVersion.VERSION_17
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                groupId = project.group as String
                artifactId = "${rootProject.name}-${project.name}"
                version = project.version as String
            }
        }

    }

    configure<AllOpenExtension> {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
        annotation("org.springframework.context.annotation.Configuration")
    }



    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType<Jar> {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            archiveFileName.set("${rootProject.name}-${project.name}-${project.version}.jar")
        }
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs += "-Xjvm-default=all"
                jvmTarget = "17"
            }
        }
    }

}
