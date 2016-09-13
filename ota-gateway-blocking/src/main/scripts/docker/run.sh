#!/usr/bin/env sh

BASE_DIR=${BASE_DIR:-"/opt/app"}
JAR=`ls ${BASE_DIR}/*.jar`

# Some colors

CL_RST='\e[0m'
CL_RED='\e[0;31m'
CL_BLU='\e[0;34m'

LEVEL_INFO="${CL_BLU}INFO${CL_RST} "
LEVEL_WARN="${CL_RED}WARN${CL_RST} "
LEVEL_ERROR="${CL_RED}ERROR${CL_RST}"

if [ -z "${JVM_HEAP_MEMORY_SIZE}" ]; then
    JVM_HEAP_MEMORY_SIZE="512"
    echo -e "${LEVEL_INFO} JVM_HEAP_MEMORY_SIZE env variable not specified. Using ${JVM_HEAP_MEMORY_SIZE} as default"
fi

if [ -z "${JVM_DIRECT_MEMORY_SIZE}" ]; then
    JVM_DIRECT_MEMORY_SIZE="512"
    echo -e "${LEVEL_INFO} JVM_DIRECT_MEMORY_SIZE env variable not specified. Using ${JVM_DIRECT_MEMORY_SIZE} as default"
fi

CAN_START=true
PARAMS=""

if [ -z "${REDIS_NODES}" ]; then
    echo -e "${LEVEL_ERROR} REDIS_NODES env var not specified. This SOTAC instance can't store session information" 1>&2
    CAN_START=false
fi

if [ "${CAN_START}" = true ]; then

java \
-server \
-Xms${JVM_HEAP_MEMORY_SIZE}m \
-Xmx${JVM_HEAP_MEMORY_SIZE}m \
-XX:MaxDirectMemorySize=${JVM_DIRECT_MEMORY_SIZE}M \
-XX:MaxMetaspaceSize=100m \
-XX:+UseParallelGC \
-XX:+DisableExplicitGC \
-XX:+UseStringDeduplication \
-XX:+AggressiveOpts \
-Dsun.rmi.dgc.client.gcInterval=3600000 \
-Dsun.rmi.dgc.server.gcInterval=3600000 \
-XX:OnOutOfMemoryError="kill -9 %p" \
-verbose:gc \
-XX:+PrintGCDetails \
-XX:+PrintGCTimeStamps \
-XX:+PrintGCDateStamps \
-XX:+PrintAdaptiveSizePolicy \
-XX:+PrintTenuringDistribution \
-XX:+PrintStringDeduplicationStatistics \
-Dio.netty.leakDetectionLevel=${NETTY_LEAK_DETECTION_LEVEL:-simple} \
${PARAMS} \
-jar ${JAR}
    exit 0
else
    echo -e "${LEVEL_ERROR} Some of mandatory environment variables variables was not specified. Exiting"
    exit 1
fi

