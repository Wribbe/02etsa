#!/bin/sh

directory="$1"
echo ${directory}

cd ${directory}
javac Main.java
compilation_result=$?
if [ "$compilation_result" != 0 ]; then
    echo "Compilation error.. aborting."
    exit
fi
cd ..
java ${directory}.Main
