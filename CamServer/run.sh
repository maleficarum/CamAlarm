#!/bin/sh

cd build

version="1.1"
export CP=lib/CamServer-$version.jar 
for i in $(ls -1 external/lib/*jar); do CP=$CP:$i; done

echo "Classpath $CP"

java -cp $CP mx.angellore.cam.alarms.Main
