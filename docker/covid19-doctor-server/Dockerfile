FROM doctor-app-server-base AS doctor-app-server-builder
LABEL stage=intermediate
RUN apt-get -y install maven git openssh-client openjdk-8-jdk && apt-get clean
RUN mkdir /Covid19-DoctorsApp

ARG _env

WORKDIR /Covid19-DoctorsApp
COPY pom.xml .
COPY settings.xml .
COPY src ./src/
RUN if [ "x$_env" = "x" ] ; \
        then mvn clean install -Pdevelopment --settings settings.xml ; \
        else mvn clean install -Pproduction --settings settings.xml ; \
    fi

# ============== BUILD IMAGE
FROM doctor-app-server-base AS doctor-app-server
COPY docker/covid19-doctor-server/data /

WORKDIR /var/opt/Covid19-DoctorsApp

COPY --from=doctor-app-server-builder /Covid19-DoctorsApp/target/Covid19-DoctorAppServer-*.jar target/

ENTRYPOINT [ "/init" ]
