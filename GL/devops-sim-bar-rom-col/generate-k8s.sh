#!/bin/sh
# Check parameters
if [ $# -ne 3 ]; then
  echo "usage: $0 app_name image kube_token"
  exit 1
fi
# Initialisation
echo "Running ad-hoc script    generate-k8s.sh"
mkdir k8s-deploy
# Extraction of command line parameters
app_name=$1
host=$1
image=$(echo "$2" | sed 's/[\/&]/\\&/g') # Escape slashes '/'
token=$3

## Transformation of the deploy.yaml.orig file
sed "s/<APP>/$app_name/g" deploy.yaml.orig > k8s-deploy/deploy.yaml
sed -i "s/<IMAGE>/$image/g" k8s-deploy/deploy.yaml
sed -i "s/<HOST>/$host/g" k8s-deploy/deploy.yaml

## Transformation of the kube.config.orig file
sed "s/<KUBE_TOKEN>/$token/g" kube.config.orig > kube.config

## For debug : display kube.config file
echo "Transformed kube.config :"
cat kube.config

echo "Transformed deploy.yaml :"
cat k8s-deploy/deploy.yaml
