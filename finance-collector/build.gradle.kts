import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"

    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.hidetake.ssh") version "2.10.1"
}

group = "app.properstock"
version = "0.0.1-SNAPSHOT"
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

    implementation("org.springdoc:springdoc-openapi-ui:1.5.11")
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.5.11")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.11")
    implementation("org.seleniumhq.selenium:selenium-java:3.141.59")

    implementation("org.jsoup:jsoup:1.14.3")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.mapstruct:mapstruct:1.4.2.Final")

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

val dockerImageName = "home.jowookjae.in:5000/ppst.finance-collector:${project.version}"

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
    devServer = withGroovyBuilder {
        "create"("remoteName") {
            setProperty("host", "home.jowookjae.in")
            setProperty("user", "admin")
            setProperty("password", "admin")
        }
    }
}

tasks.register("deploy.dev") {
    dependsOn("bootBuildImage").doLast {
        ssh.run(delegateClosureOf<RunHandler> {
            session(
                devServer,
                delegateClosureOf<SessionHandler> {
                    execute(hashMapOf("ignoreError" to true), "docker stop ${project.name}")
                    execute(hashMapOf("ignoreError" to true), "docker rm ${project.name}")
                    execute(hashMapOf("ignoreError" to true), "docker rmi $dockerImageName")
                    execute("docker run -e SPRING_PROFILES_ACTIVE=dev -p 6001:8080 -p 16001:8443 --name ${project.name} -d $dockerImageName")
                }
            )
        })
    }
}
