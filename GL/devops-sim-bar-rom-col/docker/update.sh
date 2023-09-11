#!/bin/bash

cd ../

docker build -f docker/Dockerfile_mvn_ant_optim -t registry.forge.hefr.ch/data-cockpit/javanco-legacy/mvn_ant_opt .
docker build -f docker/Dockerfile_mvn_ant -t registry.forge.hefr.ch/data-cockpit/javanco-legacy/mvn_ant_base .
docker push registry.forge.hefr.ch/data-cockpit/javanco-legacy/mvn_ant_opt
docker push registry.forge.hefr.ch/data-cockpit/javanco-legacy/mvn_ant_base
