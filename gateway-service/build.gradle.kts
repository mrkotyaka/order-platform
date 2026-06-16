plugins {
    id("org.springframework.boot")
}

version = "2.0.0"

//val springCloudVersion by extra("2024.0.1")

dependencies {
    implementation(project(":common-libs"))

    // ВАЖНО: Используется реактивный Gateway, а не стандартный Web (no starter-web!)
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.1.5")

    // Библиотеки для работы с JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

//dependencyManagement {
//    imports {
//        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
//    }
//}

tasks.test {
    useJUnitPlatform()
}