#!/bin/bash

set -f
AVERAGE=360

RESULT=$(curl --silent https://concsys-admin.kube.isc.heia-fr.ch/api/v1/check/microservices/concsys-22-grp-ex2-bar-ter 2>&1)
echo 'Check detailled result here: https://concsys-admin.kube.isc.heia-fr.ch/api/v1/check/microservices/concsys-22-grp-ex2-bar-ter'

FINAL_SCORE=$(grep 'Final score' <<< $RESULT )
echo "result: $FINAL_SCORE"

POINT=$(sed "s@^[^0-9]*\\([0-9]\\+\\).*@\\1@" <<< $FINAL_SCORE )
echo "point: $POINT"

if (( $POINT >= $AVERAGE )); then
    echo "Test passed"
    exit 0
else
    echo "Test failed"
    exit 1
fi
