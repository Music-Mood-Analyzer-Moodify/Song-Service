plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.moodify"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	compileOnly("org.projectlombok:lombok:1.18.38")
	annotationProcessor("org.projectlombok:lombok:1.18.38")
	implementation("io.micrometer:micrometer-registry-otlp:1.15.0-M3")
	implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:2.12.0")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-security:3.4.4")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testCompileOnly("org.projectlombok:lombok:1.18.38")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.withType<Test> {
	useJUnitPlatform()
}