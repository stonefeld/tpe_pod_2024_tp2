#!/bin/bash

cd "$(dirname "$0")"

print_error() {
  echo "$(basename $0): $1"
  exit 1
}

print_help() {
  # TODO: usage
  printf "Usage"
  exit 0
}

for i in "$@"; do
  case $i in
    -D*) JAVA_OPTS="$JAVA_OPTS $i";;
    -h|--help) print_help;;
    *) print_error "Unknown argument $i";;
  esac
done

MAIN_CLASS="ar.edu.itba.pod.hazelcast.client.YTDCollectionClient"

java $JAVA_OPTS -cp 'lib/jars/*' $MAIN_CLASS $*

