FROM amazoncorretto:17-alpine
VOLUME /tmp
RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /tmp
COPY ./applications/app-service/build/libs/franchises-ms.jar franchises-ms.jar
RUN sh -c 'touch /franchises-ms.jar'
ENV TZ=America/Bogota
USER appuser
ENTRYPOINT [ "sh", "-c", "java -jar franchises-ms.jar" ]
