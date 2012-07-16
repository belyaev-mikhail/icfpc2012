#!/bin/bash
cp /dev/null /tmp/report
for i in /home/lonlylocly/icfp/hola/maps/*.map 
do 
    echo $i >> /tmp/report
    cat $i | java -jar ../HolaVis/out/artifacts/HolaVis_jar/HolaVis.jar >> /tmp/report
    echo >> /tmp/report
done
