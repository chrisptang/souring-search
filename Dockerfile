FROM openjdk:8-jre-alpine
LABEL maintainer="chris.p.tang@gmail.com"

ENV APP_LOG_LEVEL "info"
ENV APP_NAME "sourcing-search"
ENV SERVER_PORT "1688"
ENV HOME_LOG "/data/logs"
ENV BASE_JVM_OPTS "-server -Xmx512m -Xms512m"
ENV GC_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=70 \
-XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/data/logs/heap_dump -Xloggc:/data/logs/gc.log"
ENV LOG_RECEIVED_PATH "/data/logs/received"
ENV TZ="Asia/Shanghai"

COPY target/sourcing-search-controller-0.0.1-SNAPSHOT.jar /app.jar

CMD java ${BASE_JVM_OPTS} ${GC_OPTS} ${JVM_OPTS} -Denv=${DEPLOY_ENV} -Dlogging.level.root=${APP_LOG_LEVEL} \
-Dapp.id=${APP_NAME} -Dserver.port=${SERVER_PORT} -Dlog.root.path=${LOG_RECEIVED_PATH} -Dlog.home=${HOME_LOG} -jar /app.jar
EXPOSE 1688