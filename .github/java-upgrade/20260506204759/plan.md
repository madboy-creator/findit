# Upgrade Plan: findit (20260506204759)

- **Generated**: May 6, 2026 20:50
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

## Available Tools

**JDKs**
- JDK 17: not available (baseline will be skipped)
- JDK 21: **<TO_BE_INSTALLED>** (required by step 1)
- JDK 26.0.1: C:\Program Files\Java\jdk-26.0.1\bin (available, but target runtime is Java 21)

**Build Tools**
- Maven 3.9.15: C:\maven\apache-maven-3.9.15\bin
- Maven Wrapper: absent (system Maven will be used)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

- Upgrade Java runtime to the latest LTS version, Java 21.

## Options

- Working branch: appmod/java-upgrade-20260506204759
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade project Java runtime and build target from Java 17 to Java 21.

## Technology Stack

| Technology/Dependency           | Current | Min Compatible | Why Incompatible                                    |
| ------------------------------ | ------- | -------------- | --------------------------------------------------- |
| Java                           | 17      | 21             | User requested latest LTS runtime                   |
| Spring Boot                    | 3.4.5   | 3.4.5          | Current version supports Java 21; no upgrade needed |
| Maven                          | 3.9.15  | 3.9.0          | Required for Java 21 compilation                    |
| maven-compiler-plugin          | inherited | 3.11.0       | Required for `--release 21` support                 |
| spring-boot-maven-plugin       | 3.4.5   | 3.4.5          | Compatible with Java 21 when using Spring Boot 3.4 |

## Derived Upgrades

- Java 21 requires Maven 3.9+; system Maven 3.9.15 is already available.
- Install JDK 21 because the current environment only includes JDK 26 and the base JDK 17 is missing.
- Keep Spring Boot at 3.4.5 to minimize risk while meeting the Java 21 runtime goal.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: A Java 21 JDK is required to compile and validate the target runtime level.
  - **Changes to Make**:
    - Install or make JDK 21 available on the machine.
    - Confirm system Maven 3.9.15 is available and usable.
    - Document that no Maven wrapper exists and system Maven will be used.
  - **Verification**: `java -version && mvn -version` with JDK 21 and Maven 3.9.15

- Step 2: Setup Baseline
  - **Rationale**: Baseline validation normally verifies current behavior before upgrade.
  - **Changes to Make**:
    - Skip baseline because the current project JDK 17 is not available in the environment.
  - **Verification**: skipped because base JDK is unavailable

- Step 3: Update Java target configuration
  - **Rationale**: The project must explicitly target Java 21 for compilation and runtime compatibility.
  - **Changes to Make**:
    - Update `<java.version>` in `pom.xml` from `17` to `21`.
    - Validate inherited Spring Boot plugin configuration supports Java 21.
    - Keep dependency versions unchanged unless build errors indicate an immediate compatibility issue.
  - **Verification**: `mvn clean test-compile -q` with JDK 21

- Step 4: Final Validation
  - **Rationale**: Confirm the upgrade and ensure the project builds cleanly with the target runtime.
  - **Changes to Make**:
    - Run a full Maven verification cycle with JDK 21.
    - Fix any Java 21 compatibility issues discovered during compilation.
  - **Verification**: `mvn clean test -q` with JDK 21

## Key Challenges

- **Missing base JDK 17 for baseline**
  - **Challenge**: The current runtime baseline cannot be reproduced in this environment.
  - **Strategy**: Skip baseline and rely on compile validation targeting Java 21.

- **Java 21 runtime upgrade**
  - **Challenge**: Project currently targets Java 17 and must move to a newer language/runtime level.
  - **Strategy**: Update `pom.xml` and validate with JDK 21 and Maven 3.9.15.

- **No Maven wrapper present**
  - **Challenge**: Build reproducibility depends on system Maven rather than a repo-bound wrapper.
  - **Strategy**: Use verified Maven 3.9.15 and document the absence of a wrapper.

- **No test sources found**
  - **Challenge**: Final validation will have no existing test classes to exercise runtime behavior.
  - **Strategy**: Use clean compile and `mvn test` to verify build correctness and call out the coverage limitation.
