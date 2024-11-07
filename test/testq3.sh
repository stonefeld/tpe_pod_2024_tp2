#!/bin/bash

print_error() {
    echo -e "\e[31m$1\e[0m"
    exit 1
}

echo "Query 3: Initializing server"
cd ..
cd ./bin/server
./server.sh > /dev/null 2>&1 &


PID=$!
cd ../..

sleep 6

mkdir -p ./test/results

echo "Query 3: Running client"

[ -d ./bin/client ] || print_error "Client directory not found"
cd ./bin/client

./query3.sh -Daddresses=127.0.0.1:5701 -Dcity=NYC  -DinPath=../../test/data/ -DoutPath=../../test/results/ -Dn=2 -Dfrom=01/01/2000 -Dto=31/12/2023 > /dev/null 2>&1
sleep 3

cd ../..

echo "Query 3: Killing server"

if [ ! -z "$PID" ]; then
    PPID_COL=$(ps aux | head -n 1 | tr -s ' ' '\n' | nl | grep -w "PPID" | awk '{print $1}')
    PID_COL=$(ps aux | head -n 1 | tr -s ' ' '\n' | nl | grep -w "PID" | awk '{print $1}')
    ps aux | awk -v pid="$PID" -v ppid_col="$PPID_COL" -v pid_col="$PID_COL" '{ if ($ppid_col == pid) print $pid_col }' | xargs -r kill
    kill $PID

else
    echo "Process not found."
fi
sleep 5

echo "Query 3: Testing results"

diff -q ./test/results/query3.csv ./test/expected/expected-q3.csv >> /dev/null

if [ $? -eq 0 ]; then
    echo -e "\e[32mQuery 3: TEST PASSED\e[0m"
else
    echo -e "\e[31mQuery 3: FAILED\e[0m"
fi