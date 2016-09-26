#!/usr/bin/env sh

BASE_DIR=${BASE_DIR:-"/opt/app"}
JAR=`ls ${BASE_DIR}/*.jar`

trap 'kill -TERM ${JAVA_PID}' TERM INT

java \
-server \
-Xms${JVM_HEAP_MEMORY_SIZE:-512}m \
-Xmx${JVM_HEAP_MEMORY_SIZE:-512}m \
-XX:MaxDirectMemorySize=${JVM_DIRECT_MEMORY_SIZE:-512}M \
-XX:MaxMetaspaceSize=100m \
-XX:+UseParallelGC \
-XX:+DisableExplicitGC \
-XX:+UseStringDeduplication \
-XX:+AggressiveOpts \
-XX:OnOutOfMemoryError="kill -9 %p" \
-jar ${JAR} &

JAVA_PID=$!

wait ${JAVA_PID}
exit $?
