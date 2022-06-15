import org.gradle.api.internal.tasks.testing.junit.JUnitTestFramework

/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `java-library`
}

version = "1.0.0"
group = "org.gradle.sample"

repositories {
    mavenCentral()
}

// tag::multi-configure[]
testing {
    suites.configureEach { // <1>
        if (this is JvmTestSuite) {
            useJUnitJupiter()
            dependencies { // <2>
                implementation("org.mockito:mockito-junit-jupiter:4.6.1")
            }
        }
    }

    suites { // <3>
        val secondaryTest by registering(JvmTestSuite::class)
        val tertiaryTest by registering(JvmTestSuite::class) {
            dependencies { // <4>
                implementation("org.apache.commons:commons-lang3:3.11")
            }
        }
    }
}
// end::multi-configure[]

val checkDependencies by tasks.registering {
    dependsOn(testing.suites.getByName("test"), testing.suites.getByName("secondaryTest"), testing.suites.getByName("tertiaryTest"))
    doLast {
        assert(configurations.getByName("testRuntimeClasspath").files.size == 12)
        assert(configurations.getByName("testRuntimeClasspath").files.any { it.name == "mockito-junit-jupiter-4.6.1.jar" })
        assert(configurations.getByName("secondaryTestRuntimeClasspath").files.size == 12)
        assert(configurations.getByName("secondaryTestRuntimeClasspath").files.any { it.name == "mockito-junit-jupiter-4.6.1.jar" })
        assert(configurations.getByName("tertiaryTestRuntimeClasspath").files.size == 13)
        assert(configurations.getByName("tertiaryTestRuntimeClasspath").files.any { it.name == "mockito-junit-jupiter-4.6.1.jar" })
        assert(configurations.getByName("tertiaryTestRuntimeClasspath").files.any { it.name == "commons-lang3-3.11.jar" })
    }
}