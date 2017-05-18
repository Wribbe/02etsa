#!/bin/sh

directory="$1"
echo ${directory}

javac -cp .:../* ${directory}/*.java
compilation_result=$?
if [ "$compilation_result" != 0 ]; then
    echo "Compilation error.. aborting."
    exit
fi

command="java -cp .:../* ${directory}.Main"
echo "Running command: ${command}"
${command}
