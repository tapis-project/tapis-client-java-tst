#!/bin/sh
PrgName=`basename $0`
# Determine absolute path to location from which we are running.
export RUN_DIR=`pwd`
export PRG_RELPATH=`dirname $0`
cd $PRG_RELPATH/.
export PRG_PATH=`pwd`
cd $RUN_DIR

# Create target dir in case not yet created by maven
mkdir -p $PRG_PATH/target
# Download latest openapi spec from repo

# Dev yaml
curl -o target/openapi_v3.yml https://raw.githubusercontent.com/tapis-project/authenticator/dev/service/resources/openapi_v3.yml

# Run swagger-cli from docker image to generate bundled json file from openapi yaml file
set -xv
export REPO=$PRG_PATH/target
export API_NAME=auth.json
mkdir -p $REPO/swagger-api/out
docker run --rm -v $REPO/openapi_v3.yml:/swagger-api/yaml/openapi_v3.yml \
       	tapis/swagger-cli bundle -r /swagger-api/yaml/openapi_v3.yml > /tmp/$API_NAME
cp /tmp/$API_NAME $REPO/$API_NAME
