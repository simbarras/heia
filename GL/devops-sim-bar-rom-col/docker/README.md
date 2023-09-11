# Instructions for building environment images for javanco

This folder contains three Dockerfiles :
- Dockerfile_mvn_ant
- Dockerfile_mvn_ant_opt
- Dockerfile_vnc

The first one builds a docker image containing both an ant and maven environment, without trying to optimized whatsoever.

The second one does the same but tries to optimize the size of the image, see below.

The third one is experimental - it builds a container to which it is possible to connect using vnc.

## Cache optimization

The second image mainly optimizes by forcing maven to download the javanco libraries inside the base image. For that, it copies the `minimal_pom.xml` inside the image and runs a couple of maven calls. This `minimal_pom.xml` contains two sections of the regular `pom.xml` file :
- The dependency section, but without hard linked dependencies
- The pluginManagement seciton

If a library is added to the pom.xml of javanco, it is recommended to add it as well to the minimal_pom.xml, to rebuild the image and push it to the registry.

## Update images in the registry

For now this operation is manual. The `update.sh` script should be called. You need repositoy_push credentials on the `javanco-legacy` repository.

