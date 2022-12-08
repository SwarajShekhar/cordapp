# Dev Notes

Build and deploy nodes `./gradlew deployNodes`

Run nodes `./build/nodes/runnodes --allow-hibernate-to-manage-app-schema`

Run Spring Boot Server 

- `./gradlew runModelNServer`
- `./gradlew runManufacturer1Server`
- `./gradlew runModelNServer`
- `./gradlew runWholesaler1Server`

## UI/Frontend Development Server

Notes for devlopment mode For hot module reload and faster frontend build:

1. comment out lines in clients/gradle.build with dependencies on frontend build i.e.,

```groovy
// bootJar.dependsOn(copyWebApp)
// compileJava.dependsOn ":frontend-app:build"
```

2. update/uncomment `spring.resources.static-locations` in `clients/src/main/resources/application.properties` file
3. add/update proxy configuration in frontend-app/package.json to point to target spring boot environment e.g. `proxy": "http://localhost:8082"`
4. run React application using `npm start` and navigate to http://localhost:3000