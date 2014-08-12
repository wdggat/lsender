#!/bin/bash 

mvn clean package;
scp target/lsender-jar-with-dependencies.jar liu@121.40.98.72:~/lsender;
ssh liu@121.40.98.72 <<EOF
cd lsender;
./shutdown.sh;
./startup.sh;
EOF
echo 'redeployed.'
