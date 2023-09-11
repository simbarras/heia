#!/bin/bash
for i in {1..10}
do
    printf "\n $i: \t"
    curl echo.concsys-22-usr-simon-barras.kube.isc.heia-fr.ch/?echo_env_body=HOSTNAME
done
