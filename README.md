# emergency-alert-api

A RESTful web service .war that returns emergency alerts for use with
the emergency-alert-banner web component. Deploy to a Tomcat server that
has a MySQL JDBC connector (mysql-connector-java-{version}-bin.jar} in
the /lib directory under the Tomcat root; for example:

```
/usr/local/tomcat/lib/mysql-connector-java-5.1.44-bin.jar
```

### Compile war file into build/libs/
```
./gradlew assemble
```

Copy `build/libs/emergency-alert-api.war` to the Tomcat webapp directory;
for example `/usr/local/tomcat/webapps/`.

### Endpoints

| Endpoint | HTTP Method | Description |
| -------- | ----------- | ----------- |
| /ok      | GET | check that application is running |
| /alerts  | GET | get emergency alerts JSON         |

## Run on Tomcat server

If not already done, make
[modifications to build a war file](#modifications-to-build-war-file) then
build it and deploy to a Tomcat server.

1. Build the .war file in `build/libs`:

    ```
    ./gradlew assemble

    or

    ./gradlew bootWar

    ```

2. Copy the .war file to the `webapps` directory under the base Tomcat path
of a running Tomcat server; for example to `/usr/local/tomcat/webapps`.
The application should deploy.

You should be able to connect to it at
`http://{server name}:{port}/{war-file-name}/alerts`; for example, if the server
is localhost, port is 8080 and the .war file name is `emergency-alert-api-1.0.1.war`,
you can connect to `http://localhost:8080/emergency-alert-api-1.0.1/alerts`.

Note: if it doesn't deploy, check the `webapps` directory to see if it unpacked
the .war file. Check that the attributes `unpackWars="true"` and
`autoDeploy="true"` are set in the <Host> element in the configuration
file `/usr/local/tomcat/conf/server.xml`.

## Notes

Miscellaneous notes for those new to Gradle or Spring Boot.

### Gradle tasks

You can see all the gradle tasks by running:

 ```
 ./gradlew tasks
 ```

Here are the tasks typically used in this project:

| task | description |
| ---- | ----------- |
| bootRun  | run this project as a Spring Boot application (on stand-alone server)         |
| assemble | assemble the outputs of this project (in build/)                              |
| bootJar  | assemble executable .jar archive and include all dependencies in build/libs/  |
| bootWar  | assemble executable .war archive and include all dependencies in build/libs/  |
| build    | assembles and tests this project                                              |
| publishToMavenLocal                  | publish to the local Maven cache (repository)     |
| publishWarPublicationToByuRepository | publish the .war file to the BYU Maven repository |


The `publishing` task in `build.gradle` is configured to publish to the
*snapshot* repo if "SNAPSHOT" is detected in the `version` property in the
project, otherwise it publishes to the *release* repo. You can't publish to
the *release* repo using the same version number. Either change the version
number or delete the existing directory from the repo before publishing.

### Gradle project name

The default name of the application is defined in the `rootProject.name`
property which defaults to the name of the root project directory unless
overriden by setting the `rootProject.name` property in `settings.gradle`:

```
rootProject.name = 'emergency-alert-api'
```

### Generated .jar file name

The generated .jar file name is derived from the
[rootProject.name](#gradle-project-name) and the `version` property (in
build.gradle). To generate a .jar file with a custom name, modify `build.gradle`,
add a **bootJar** task, and set the `archiveName` property of the task:

```
bootJar {
    archiveName 'my-app.jar'
}
```

Note: the bootJar task is still using the deprecated **archiveName** property
as of version 2.1.6.RELEASE, rather than the newer archiveFileName.

### Generated .war file name

The generated .jar file name is derived from the
[rootProject.name](#gradle-project-name) and the `version` property (in
build.gradle). To generate a .war file with a custom name, modify `build.gradle`,
add a **bootWar** task, and set the `archiveName` property of the task:

```
bootWar {
   archiveName 'my-app.war'
}
```

Note: the bootWar task is still using the deprecated **archiveName** property
as of version 2.1.6.RELEASE, rather than the newer archiveFileName.

The **bootWar** task requires the **war** plugin, so add it:

```
plugins {
    id 'org.springframework.boot' version '2.1.6.RELEASE'
    id 'java'
    id 'war'
}
```

### Modifications to build WAR file

To build as a .war file and deploy to Tomcat:

1. Modify the main entry point class, `EmergencyAlertApiApplication` (the one
annotated with @SpringBootApplication) to extend SpringBootServletInitializer
like this:

    ```
    @SpringBootApplication
    public class EmergencyAlertApiApplication extends SpringBootServletInitializer {

        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
            return applicationBuilder.sources(EmergencyAlertApiApplication.class);
        }

        public static void main(String[] args) {
            SpringApplication.run(EmergencyAlertApiApplication.class, args);
        }
    }
    ```

2. Modify `build.gradle` and add the *war* plugin:

    ```
    plugins {
        id 'org.springframework.boot' version '2.1.6.RELEASE'
        id 'java'
        id 'war'
    }
    ```

3. Modify `build.gradle` and add the `spring-boot-starter-tomcat` dependency
**providedCompile** like this:

    ```
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-websocket'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        providedCompile 'org.springframework.boot:spring-boot-starter-tomcat'
    }
    ```

Note: both **compileOnly** and **providedCompile** behave like the Maven
**provided** dependency but compileOnly requires the **java** plugin and
providedCompile requires the **war** plugin. Since we're using both plugins
either would work.

### Modifications to run as stand-alone server

To run stand-alone, either with `./gradlew bootRun` or in a .jar file with
`java -jar build/libs/emergency-alert-api.jar`:

1. Modify the main entry point class, `EmergencyAlertApiApplication` (the one annotated
with @SpringBootApplication) like this:

    ```
    @SpringBootApplication
    public class EmergencyAlertApiApplication {

        public static void main(String[] args) {
            SpringApplication.run(EmergencyAlertApiApplication.class, args);
        }

    }
    ```

2. (optional) the *war* plugin is not required and can be removed from `build.gradle`:

    ```
    plugins {
        id 'org.springframework.boot' version '2.1.6.RELEASE'
        id 'java'
    }
    ```

3. (optional) the `spring-boot-starter-tomcat` dependency is not required
and can be removed from `build.gradle`:

    ```
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-websocket'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
    }
    ```

