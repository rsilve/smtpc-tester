FROM amazoncorretto:19-alpine-jdk

RUN adduser -D smtpc
WORKDIR /work

ENV DEBIAN_FRONTEND noninteractive

ADD build/distributions/smtpc-tester-1.0-SNAPSHOT.tar /work/
COPY src/main/docker/entrypoint.sh /work/entrypoint.sh

# RUN chown -R smtp:smtp /work
USER smtpc


ENTRYPOINT ["sh", "/work/entrypoint.sh"]
