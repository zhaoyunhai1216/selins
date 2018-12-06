#!/bin/sh
work_path=$(pwd)
log4j_opts="-Dlog4j.configuration=file:$work_path/../etc/resources/log4j.properties"

echo "cd $1$2"
cd "$1$2"

classpath=`find -name "*.jar"|xargs|sed "s/ /:/g"`
classpath=".:$classpath"
java $3 $log4j_opts -Dlog4j.log.parent=$work_path/../logs/workers-artifacts -Dlog4j.log.app=$2/worker -classpath ${classpath} org.cluster.application.Worker "$4" "$5" "$6" "$7" "$8" "$9" "${10}" >/dev/null 2>&1 &

