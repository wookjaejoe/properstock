import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.spring") version "1.5.30"
    kotlin("kapt") version "1.5.30"

    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.hidetake.ssh") version "2.10.1"
    id("com.google.cloud.tools.jib") version "3.3.0"
}

group = "app.properstock"
version = "0.1.2"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.11")
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.6.11")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.11")
    implementation("org.seleniumhq.selenium:selenium-java:4.4.0")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.mapstruct:mapstruct:1.5.2.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.2.Final")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val dockerImageName = "jowookjae.in:5000/${project.name}:${project.version}"

tasks.bootBuildImage {
    docker.publishRegistry {
        username = ""
        password = ""
    }

    imageName = dockerImageName
    isPublish = true
}

var devServer: Any? = null
remotes {
    val user = project.properties["user"]
    val password = project.properties["password"]
    devServer = withGroovyBuilder {
        "create"("remoteName") {
            setProperty("host", "jowookjae.in")
            setProperty("user", user)
            setProperty("password", password)
        }
    }
}

jib {
    from {
        image = "amazoncorretto:11.0.16-alpine3.16"
    }
    to {
        image = dockerImageName
        tags = setOf(project.version.toString())
        setAllowInsecureRegistries(true)
    }
    container {
        jvmFlags = listOf(
            "-Dspring.data.mongodb.host=",
            "-Dspring.data.mongodb.username=",
            "-Dspring.data.mongodb.password=",
            "-Dwebdriver.chrome.remote.url="
        )
        ports = listOf("8080")
    }
}

fun SessionHandler.executeAndIgnoreError(commandLine: String): String? {
    println(commandLine)
    return execute(hashMapOf("ignoreError" to true), commandLine)
}


tasks.register("deploy.dev") {
    val runOptions = listOf(
        "-d",
        "--restart unless-stopped",
        "-p 9080:8080",
        "--name ${project.name}",
        "-v /etc/localtime:/etc/localtime:ro"
    ).joinToString(" ")
    dependsOn("jib").doLast {
        ssh.run(delegateClosureOf<RunHandler> {
            session(
                devServer,
                delegateClosureOf<SessionHandler> {
                    executeAndIgnoreError("docker rm -f ${project.name}")
                    executeAndIgnoreError("docker rmi $dockerImageName")
                    execute("docker run $runOptions $dockerImageName")
                }
            )
        })
    }
}
