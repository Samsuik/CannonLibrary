plugins {
    id("java")
    `maven-publish`
}

group = "me.samsuik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.slf4j:slf4j-api:2.0.17")

    // testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("cannonlibrary") {
            from(components["java"])
        }
    }
}