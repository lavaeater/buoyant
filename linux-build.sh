#!/bin/bash
rm -rf ../turbo-build/out-linux

java -jar ../turbo-build/packr-all-4.0.0.jar \
     --platform linux64 \
     --jdk ../turbo-build/linux.tar.gz \
     --useZgcIfSupportedOs \
     --executable buoyant \
     --classpath ./lwjgl3/build/lib/buoyant-1.0.0.jar \
     --mainclass jam.core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G \
     --resources assets/* \
     --output ../turbo-build/out-linux

butler push ../turbo-build/out-linux lavaeater/buoyant:linux
