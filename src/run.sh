#!/bin/sh

cd etsa02
javac Main.java
compilation_result=$?
if [ "$compilation_result" != 0 ]; then
    echo "Compilation error.. aborting."
    exit
fi
cd ..
java etsa02.Main
