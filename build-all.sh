#!/bin/bash
./gradlew lwjgl3:jar
./linux-build.sh
./win-build.sh
./web-build.sh
