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

kubectl -n concsys-22-usr-simon-barras wait --timeout=60s --for=condition=ready pod -l app=distributed-store-app
