#!/bin/sh
directory=${1}
javadoc -cp .:../* -d doc ${directory}
