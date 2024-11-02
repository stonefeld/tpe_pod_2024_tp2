#!/bin/bash

cd "$(dirname "$0")"

print_error() {
  echo "$(basename $0): $1"
  exit 1
}

print_help() {
  printf "Usage: ./$(basename $0) [-Dinterfaces='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' | -DclusterName=XXX | -DclusterPassword=XXX]
\t-Dinterfaces: Hazelcast cluster addresses
\t-DclusterName: Hazelcast cluster name
\t-DclusterPassword: Hazelcast cluster password

This parameters are optional, if not provided the default values will be used.\n"
  exit 0
}

for i in "$@"; do
  case $i in
    -D*) JAVA_OPTS="$JAVA_OPTS $i";;
    -h|--help) print_help;;
    *) print_error "Unknown argument $i";;
  esac
done

MAIN_CLASS="ar.edu.itba.pod.hazelcast.server.Server"

java $JAVA_OPTS -cp 'lib/jars/*' $MAIN_CLASS $*

