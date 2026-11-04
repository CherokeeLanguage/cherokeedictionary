# Multi-stage build — cherokeedictionary#124 (stage-A shape):
# JDK 8 build stage producing the Boot-executable WAR, Temurin 8 JRE runtime,
# launch java -Dgrails.env=production -jar; no Tomcat image layer, no context
# path — the app owns '/'.
FROM eclipse-temurin:8-jdk AS build
WORKDIR /src
COPY . /src
# Gradle 5.6.4 wrapper, cold cache; the vendored lib/ set resolves the four
# jalagi libraries (cherokeedictionary#120). -Dgrails.env=prod mirrors the
# canonical cedproject flow's `grails prod war` build-time environment.
RUN ./gradlew --no-daemon clean build -x test -Dgrails.env=prod

FROM eclipse-temurin:8-jre AS runtime
RUN useradd --system --uid 1001 --user-group dictapp \
    && mkdir -p /app && chown dictapp:dictapp /app
COPY --from=build /src/build/libs/dictionary-0.1.war /app/app.war
USER dictapp
ENV JAVA_TOOL_OPTIONS=-Xmx2048m
WORKDIR /app
ENTRYPOINT ["java", "-Dgrails.env=production", "-jar", "/app/app.war"]
