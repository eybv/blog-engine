FROM gradle:jdk15-openj9 as builder
WORKDIR /home/gradle/project
COPY . .
RUN gradle clean build

FROM tomcat:10.0-jdk15-adoptopenjdk-openj9
COPY --from=builder /home/gradle/project/configuration/tomcat /usr/local/tomcat/conf
COPY --from=builder /home/gradle/project/build/libs/blog-engine-*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
EXPOSE 8443
