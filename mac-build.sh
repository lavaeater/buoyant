#!/bin/bash
rm -rf ../turbo-build/out-mac

java -jar ../turbo-build/packr-all-4.0.0.jar \
     --platform mac \
     --jdk ../turbo-build/mac.tar.gz \
     --useZgcIfSupportedOs \
     --executable buoyant \
     --classpath ./lwjgl3/build/lib/buoyant-1.0.0.jar \
     --mainclass jam.core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G \
     --resources assets/* \
     --output ../turbo-build/out-mac

butler push ../turbo-build/out-mac lavaeater/buoyant:mac
