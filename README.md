# E2E Test Automation Framework

A Java and Maven-based test automation framework organized around Cucumber and behavior-driven development (BDD). The project brings together API testing, web UI automation, and database utilities in a single test codebase.

> This README is based on the project's `pom.xml` and directory structure. Implementation-specific details—including environment selection, authentication, browser lifecycle, and report output—must be verified in the corresponding Java classes.

## Technology Stack

Versions below match the supplied `pom.xml`; they are not claims about the latest available releases.

| Technology / Library | Version | Purpose |
| --- | --- | --- |
| Java | 17 | Compiler source and target version |
| Maven | Installed locally | Dependency and build management |
| Cucumber Java | 7.3.0 | Java step definitions for Gherkin scenarios |
| Cucumber JUnit | 7.3.0 | Cucumber execution through JUnit |
| REST Assured | 4.5.1 | REST API testing |
| Selenium Java | 4.18.1 | Web UI automation |
| JavaFaker | 1.0.2 | Test data generation |
| Jackson Databind | 2.15.1 | JSON serialization and deserialization |
| Lombok | 1.18.20 | Annotations for reducing Java boilerplate |
| PostgreSQL JDBC | 42.2.10 | PostgreSQL connectivity |
| Cucumber Reporting Plugin (`me.jvt.cucumber`) | 7.3.0 | Cucumber reporting integration |
| Log4j API / Core | 2.17.2 | Logging |
| Maven Surefire Plugin | 3.0.0-M3 | Test execution through Maven |

The Maven group ID and Java package name differ: test code is located under `com.bookit`.

## Project Structure

```text
e2e-test-automation-framework/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/bookit/
│       │       ├── pages/
│       │       │   ├── SelfPage.java
│       │       │   ├── SignInPage.java
│       │       │   └── TopNavigationBar.java
│       │       ├── runners/
│       │       │   └── CukesRunner.java
│       │       ├── step_definitions/
│       │       │   ├── APIStepDefs.java
│       │       │   ├── EnvStepDefs.java
│       │       │   ├── Hooks.java
│       │       │   └── MyInfoStepDefs.java
│       │       └── utilities/
│       │           ├── BookitUtils.java
│       │           ├── BrowserUtils.java
│       │           ├── ConfigurationReader.java
│       │           ├── DB_Util.java
│       │           ├── Driver.java
│       │           └── Environment.java
│       └── resources/
│           ├── Environments/
│           │   ├── qa1.properties
│           │   ├── qa2.properties
│           │   └── qa3.properties
│           ├── features/
│           │   ├── campuses.feature
│           │   ├── env_variables.feature
│           │   └── user.feature
│           ├── bookit.sql
│           └── log4j2.properties
├── .gitignore
├── config.properties
└── pom.xml
```

## Framework Organization

| Location | Responsibility / configuration to review |
| --- | --- |
| `pages` | Page and UI component classes for organizing browser interactions |
| `runners` | Cucumber entry point; review feature paths, glue packages, tags, and reporting plugins here |
| `step_definitions` | Java implementations of Gherkin steps |
| `Hooks.java` | Class reserved for scenario lifecycle behavior; inspect its implementation for actual setup and teardown |
| `utilities` | Shared helpers for configuration, browser operations, database access, and application-specific behavior |
| `resources/features` | Gherkin feature files and scenarios |
| `resources/Environments` | QA environment property files |
| `config.properties` | Root-level configuration file |
| `bookit.sql` | SQL resource; review its contents and target database before execution |
| `log4j2.properties` | Log4j configuration resource |

Conceptual execution flow:

```text
Feature → CukesRunner → Step Definitions → Page / Utility → Target System
```

API scenarios can interact with API utilities directly without using page classes.

## Prerequisites

- JDK 17
- Apache Maven
- Access to the project files
- Network access to the application and API environments under test
- A browser and driver configuration compatible with the project's `Driver` implementation for UI tests
- Access to the relevant PostgreSQL environment for database-dependent tests

Verify your installation:

```bash
java -version
mvn -version
```

Confirm that Maven is using Java 17.

## Getting Started

1. Obtain a local copy of the repository.
2. Open a terminal in the project root, where `pom.xml` is located.
3. If using an IDE, import the project as a Maven project and resolve dependencies.
4. Set the project SDK and Maven runtime JDK to Java 17.
5. Configure the required environment and access credentials.
6. Compile the test code without running tests:

```bash
mvn test-compile
```

## Environment Configuration

The project contains the following environment files:

```text
src/test/resources/Environments/qa1.properties
src/test/resources/Environments/qa2.properties
src/test/resources/Environments/qa3.properties
```

Before running tests:

1. Review the existing keys in `config.properties`.
2. Verify URLs and access settings in the relevant QA environment file.
3. Inspect `ConfigurationReader.java` and `Environment.java` to determine how configuration files are selected and loaded.
4. Review `Driver.java` for UI configuration and `DB_Util.java` for database configuration.

> The environment selection property is not assumed in this README. A command such as `-Denv=qa1` works only if the Java implementation reads that system property. Likewise, `-Dbrowser=chrome` requires corresponding support in the driver configuration.

## Running Tests

Run all commands from the project root.

### Run with Maven

```bash
mvn test
```

To remove previous build output before running:

```bash
mvn clean test
```

The current Surefire configuration includes runner classes matching this pattern:

```xml
<includes>
    <include>**/*CukesRunner.java</include>
</includes>
```

The features and scenarios executed also depend on the Cucumber configuration in `CukesRunner.java`.

### Select the runner explicitly

```bash
mvn test -Dtest=CukesRunner
```

### Run from an IDE

Open `src/test/java/com/bookit/runners/CukesRunner.java` and run it as a JUnit test.

### Filter by tag

If your feature files contain the corresponding tags, you can filter execution:

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

For a combined expression:

```bash
mvn test -Dcucumber.filter.tags="@regression and not @wip"
```

`@smoke`, `@regression`, and `@wip` are example tags; their presence in the existing feature files is not assumed.

## Surefire Execution Settings

The current `pom.xml` contains:

```xml
<parallel>methods</parallel>
<useUnlimitedThreads>true</useUnlimitedThreads>
<testFailureIgnore>true</testFailureIgnore>
<runOrder>Alphabetical</runOrder>
```

- `parallel=methods` and `useUnlimitedThreads=true` configure parallel execution. With Cucumber's JUnit 4 integration, feature files run in parallel; scenarios within the same feature run on the same thread.
- `runOrder=Alphabetical` configures the ordering of Surefire test classes. It does not guarantee alphabetical execution of Gherkin scenarios.
- `testFailureIgnore=true` allows the build to continue despite test failures. `BUILD SUCCESS` alone does not prove that all tests passed.

If test failures should fail a CI/CD pipeline, change the POM setting to:

```xml
<testFailureIgnore>false</testFailureIgnore>
```

Before relying on parallel execution, review shared WebDriver instances, authentication tokens, test data, and database connections for thread safety. The POM configuration alone does not establish that the framework is safe for parallel execution.

## Reports and Logging

Surefire test results are generated by default under:

```text
target/surefire-reports/
```

Cucumber report formats and output paths depend on the plugin settings in `CukesRunner.java`. Including a reporting dependency in the POM does not, by itself, guarantee that a particular HTML report is generated.

Logging configuration is located at:

```text
src/test/resources/log4j2.properties
```

Whether logs are written to the console, files, or both depends on that configuration.

## Adding a New Test

1. Add a scenario to an existing or new `.feature` file under `src/test/resources/features`.
2. Implement the required steps in `com.bookit.step_definitions`, reusing existing step definitions where appropriate.
3. Keep UI interactions in the `pages` layer.
4. Place reusable operations in an appropriate utility class.
5. Verify that the runner includes the feature location and step definition package.
6. Add meaningful tags when needed.
7. Run the test and review its results.

## Troubleshooting

| Issue | What to check |
| --- | --- |
| No tests are found | Runner name, Surefire include pattern, runner feature paths, and tag filters |
| Steps are reported as undefined | Step expressions, annotations, and runner glue packages |
| Java compilation fails | `java -version`, `mvn -version`, IDE SDK, and Maven runtime JDK |
| Browser does not start | `Driver.java`, browser installation, and driver management |
| API requests fail | Active environment, endpoint URL, authentication, and network/VPN access |
| Database connection fails | JDBC URL, credentials, network permissions, and `DB_Util.java` |
| Build succeeds despite failed tests | `testFailureIgnore=true` and Surefire reports |
| Parallel runs are unstable | Shared state, WebDriver lifecycle, and conflicting test data |

## Security and Maintenance

- Do not commit passwords, tokens, or private connection details.
- Review local configuration files before sharing them, and update `.gitignore` where needed.
- Environment variables and secret managers require explicit support in the configuration-loading implementation.
- Avoid exposing sensitive data in logs and reports.
- When updating dependencies, verify Java, Cucumber, Selenium, and reporting integration compatibility together.
- Do not execute `bookit.sql` outside an authorized and appropriate test environment.
