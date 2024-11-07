#!/bin/bash

./tpe_builder.sh -c -s -C

cd test

for file in ./*; do

    if [ ! -f $file ]; then
        continue
    fi
    echo "===== RUNNING TEST $file ====="
    $file
done