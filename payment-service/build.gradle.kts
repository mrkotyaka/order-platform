plugins {
    id("org.springframework.boot") //version "3.5.7"
}

version = "2.0.0"

dependencies {
    implementation(project(":common-libs"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // additional libs
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    implementation("org.liquibase:liquibase-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
}