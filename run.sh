#!/bin/bash
echo "Compiling..."
javac -cp sqlite-jdbc.jar -d bin src/*.java
echo "Starting Inventory App..."
java -cp "sqlite-jdbc.jar:bin" Main
