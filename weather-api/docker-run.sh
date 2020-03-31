#!/usr/bin/env bash
set -e
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

if [ $# -eq 0 ]; then
  echo "Usage: $0 [image-id|repository:tag]"
  echo -e "Expecting the image id ${YELLOW}sha${NC} or ${YELLOW}repository:tag${NC}"
  exit 1
fi

if [ -z $POSTGRES_PASSWORD ]; then
  printf "Ensure you have the ${YELLOW}POSTGRES_PASSWORD${NC} variable is set\n"
  exit 1
fi

if [ -z $MAPQUEST_API_KEY ]; then
  printf "Ensure you have the ${YELLOW}MAPQUEST_API_KEY${NC} variable is set\n"
  exit 1
fi

docker run --rm \
  -p 8080:8080  \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  $1
