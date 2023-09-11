#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

set -e

export REVISION=$(git rev-parse --short=8 HEAD)
export TAG=${1:-$REVISION}

echo "TAG: $TAG"

for f in $SCRIPTPATH/*.yaml; do
  echo " - Processing $f file..";
  # envsubst < $f | cat
  envsubst < $f | kubectl apply -f -
done

kubectl -n default wait --timeout=60s --for=condition=ready pod -l app=gateway
kubectl -n default wait --timeout=60s --for=condition=ready pod -l app=drink-rest
kubectl -n default wait --timeout=60s --for=condition=ready pod -l app=rating-rest
kubectl -n default wait --timeout=60s --for=condition=ready pod -l app=drink-mqtt
kubectl -n default wait --timeout=60s --for=condition=ready pod -l app=rating-mqtt
