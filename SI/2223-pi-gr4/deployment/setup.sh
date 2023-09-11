#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

set -e

export KUBECONFIG=$SCRIPTPATH/kubeconfig
export REVISION=$(git rev-parse --short=8 HEAD)
export TAG=${1:-$REVISION}
export URL=${2:-"localhost"}

echo "TAG: $TAG"
echo "URL: $URL"

for f in $SCRIPTPATH/deployment_*.yml; do
  echo " - Processing $f file..";
  # envsubst < $f | cat
  envsubst < $f | kubectl apply -f -
done
sleep 10
kubectl -n pi-gr4 wait --timeout=180s --for=condition=ready pod -l app=frontend
kubectl -n pi-gr4 wait --timeout=180s --for=condition=ready pod -l app=backend
