#!/bin/sh
work_path=$(pwd)
log4j_opts="-Dlog4j.configuration=file:$work_path/../etc/resources/log4j.properties"

echo "cd $1"
cd $1

classpath=`find -name "*.jar"|xargs|sed "s/ /:/g"`
classpath=".:$classpath"

nohup java $5 $log4j_opts -Dlog4j.log.parent=$work_path/../logs/workers-artifacts -Dlog4j.log.app=$3"_"$6"_"$7/worker -classpath ${classpath} org.cluster.application.Worker --host=$2 --appID=$3 --appClass=$4 --seq=$6 --total=$7 --yaml=$8 >/dev/null 2>&1 &
