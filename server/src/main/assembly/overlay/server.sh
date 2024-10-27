#!/bin/bash

cd "$(dirname "$0")"

MAIN_CLASS="ar.edu.itba.pod.hazelcast.server.Server"

java  $JAVA_OPTS -cp 'lib/jars/*' $MAIN_CLASS $*
