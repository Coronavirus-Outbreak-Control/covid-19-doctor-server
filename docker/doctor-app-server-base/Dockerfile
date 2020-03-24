FROM ubuntu:latest AS doctor-app-server-base

# install some prerequisites needed by adding GPG public keys (could be removed later)
ADD https://github.com/just-containers/s6-overlay/releases/download/v1.22.1.0/s6-overlay-amd64.tar.gz /tmp/
RUN tar xzf /tmp/s6-overlay-amd64.tar.gz -C /

RUN apt-get update && \
    apt-get -y install --no-install-recommends \
        wget ca-certificates openjdk-8-jre vim procps psmisc && \
        apt-get clean

EXPOSE 80