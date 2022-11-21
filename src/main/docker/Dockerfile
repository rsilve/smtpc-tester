FROM openjdk:17.0-slim

RUN groupadd -r smtp && useradd -r -s /bin/false -g smtp smtp
WORKDIR /work

ENV DEBIAN_FRONTEND noninteractive

ADD build/distributions/smtpc-tester-1.0-SNAPSHOT.tar /work/
COPY src/main/docker/entrypoint.sh /work/entrypoint.sh

RUN chown -R smtp:smtp /work
USER smtp


ENTRYPOINT ["bash", "/work/entrypoint.sh"]
