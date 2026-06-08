plugins {
    id("org.springframework.boot")
}

version = "1.0.0"

val springCloudVersion by extra("2024.0.0")

dependencies {
    implementation(project(":common-libs"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.kafka:spring-kafka")

//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    runtimeOnly("org.postgresql:postgresql")

    // lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

//    // additional libs
//    implementation("org.mapstruct:mapstruct:1.6.3")
//    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
//    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
//    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Этот стартер автоматически добавит JavaMailSender и всё необходимое
    implementation("org.springframework.boot:spring-boot-starter-mail")

    implementation("org.springframework.boot:spring-boot-starter-validation")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}

tasks.test {
    useJUnitPlatform()
}