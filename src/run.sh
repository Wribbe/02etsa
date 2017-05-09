#!/bin/sh

directory="$1"
echo ${directory}

cd ${directory}
javac *.java
compilation_result=$?
if [ "$compilation_result" != 0 ]; then
    echo "Compilation error.. aborting."
    exit
fi
cd ..

command="java ${directory}.Main"
echo "Running command: ${command}"
${command}
