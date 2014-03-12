#!/bin/sh
kill `ps aux |grep "lsender-jar-with-dependencies.jar" |awk '{print $2}'`
