SERVER_PORT=$1
LOGNAME=jeeboss-webapp-$1
SPRING_PROFILES_ACTIVE=$2

if [ -n "$SERVER_PORT" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The port $SERVER_PORT already used!"
        exit 1
    fi
fi


#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ../
DEPLOY_DIR=`pwd`

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
LIB_JARS=$DEPLOY_DIR/conf:$LIB_JARS

JAVA_OPTS=" -Dtomcat.server.port=$SERVER_PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -Dlogname=$LOGNAME -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dio.netty.leakDetectionLevel=advanced"

JAVA_MEM_OPTS=" -server -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -Xloggc:/apps/logs/gc/$LOGNAME.log -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGC"

echo -e "nohup java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $LIB_JARS com.zksite.web.common.Application > $LOGNAME.log 2>&1 &"
echo -e "starting the server on port $SERVER_PORT ...\c"

nohup java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $LIB_JARS com.zksite.web.common.Application > $LOGNAME.log 2>&1 &


echo "OK!"
PIDS=`ps -f|grep java|grep "$SERVER_PORT" | awk '{print $2}'`
echo "PID: $PIDS"
