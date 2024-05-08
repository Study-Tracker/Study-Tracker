FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

#COPY mvnw .
#COPY .mvn .mvn
#COPY pom.xml .
#COPY client client
#COPY web web

#RUN ./mvnw clean package -DskipTests
RUN mkdir -p target/dependency
#RUN cp web/target/*.war target/dependency/study-tracker.war
COPY web/target/*.war target/dependency/study-tracker.war

FROM eclipse-temurin:21-jdk-alpine
MAINTAINER studytracker.io

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/study-tracker.war /app/study-tracker.war
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","io.studytracker.Application"]
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app/study-tracker.war ${0} ${@}"]