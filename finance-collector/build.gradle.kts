import org.hidetake.groovy.ssh.core.RunHandler
import org.hidetake.groovy.ssh.session.SessionHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"

    id("org.springframework.boot") version "2.5.4"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    implementation("org.springdoc:springdoc-openapi-ui:1.5.10")
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.5.10")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.10")
    implementation("org.springdoc:springdoc-openapi-webflux-core:1.5.10")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.seleniumhq.selenium:selenium-java:3.141.59")
    implementation("org.jsoup:jsoup:1.14.2")
    implementation("commons-io:commons-io:2.11.0")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
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

val dockerImageName = "jowookjae/ppst.finance-collector:${project.version}"

tasks.bootBuildImage {
    docker.publishRegistry {
        username = "jowookjae"
        password = "jowookjae"
    }

    imageName = dockerImageName
    isPublish = true
}

tasks.register("build.dev") {
    dependsOn("build").doLast {
        exec {
            commandLine("docker build -t $dockerImageName -f docker/Dockerfile .".split(" "))
        }
        exec {
            commandLine("docker login -u=jowookjae -p=jowookjae".split(" "))
        }
        exec {
            commandLine("docker push $dockerImageName".split(" "))
        }
    }
}

var devServer: Any? = null
remotes {
    devServer = withGroovyBuilder {
        "create"("remoteName") {
            setProperty("host", "218.147.138.41")
            setProperty("user", "dev")
            setProperty("password", "dev")
        }
    }
}

tasks.register("deploy.dev") {
    dependsOn("build.dev").doLast {
        ssh.run(delegateClosureOf<RunHandler> {
            session(
                devServer,
                delegateClosureOf<SessionHandler> {
                    execute(hashMapOf("ignoreError" to true), "docker stop ${project.name}")
                    execute(hashMapOf("ignoreError" to true), "docker rm ${project.name}")
                    execute(hashMapOf("ignoreError" to true), "docker rmi $dockerImageName")
                    execute("docker run -e SPRING_PROFILES_ACTIVE=dev -p 5000:8080 -p 15000:8443 --name ${project.name} -d $dockerImageName")
                }
            )
        })
    }
}