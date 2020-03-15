FROM doctor-app-server-base AS doctor-app-server-builder
LABEL stage=intermediate
RUN apt-get -y install maven git openssh-client openjdk-8-jdk && apt-get clean
RUN mkdir /Covid19-DoctorsApp

WORKDIR /Covid19-DoctorsApp
COPY pom.xml .
COPY src ./src/
RUN mvn clean install

# ============== BUILD IMAGE
FROM doctor-app-server-base AS doctor-app-server
COPY docker/data /

WORKDIR /var/opt/Covid19-DoctorsApp

COPY --from=doctor-app-server-builder /Covid19-DoctorsApp/target/Covid19-DoctorAppServer-*.jar target/

ENTRYPOINT [ "/init" ]
