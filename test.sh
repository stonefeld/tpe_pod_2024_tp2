#!/bin/bash

chmod +x ./tpe_builder.sh
./tpe_builder.sh -c -s -C || exit 1
cd test || exit 1

errors=0

for file in ./*; do
    [ -f $file ] || continue
    chmod +x $file
    printf "\n===== RUNNING TEST $file =====\n"
    $file || errors=1
done

exit $errors