plugins {
    // Объявляем плагины и их версии, но не применяем к корню (apply false)
    // Это избавляет от дублирования версий в каждом модуле
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false

    // Стандартные плагины для Java
    java
    // Если планируете использовать Kotlin в коде (необязательно)
    // kotlin("jvm") version "1.9.25" apply false
}

allprojects {
    group = "ru.mrkotyaka"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // Эти настройки применятся ко ВСЕМ дочерним модулям
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    // Общие зависимости для всех модулей (например, Lombok)
    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}