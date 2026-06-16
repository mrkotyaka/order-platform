plugins {
    id("java-library")
    // Рекомендую добавить этот плагин, он магически чинит проблему с версиями
//    id("io.spring.dependency-management") version "1.1.4"
}

version = "2.0.0"

//java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(21))
//    }
//}

// Если не хотите использовать плагин dependency-management,
// создайте переменную для версии Lombok
val lombokVersion = "1.18.34"

dependencies {
    // BOM Spring Boot
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.0"))

    // Валидация (теперь версия возьмется из BOM)
    api("jakarta.validation:jakarta.validation-api")

    // Lombok - исправленные конфигурации и добавленные версии
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}