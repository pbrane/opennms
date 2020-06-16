#!/usr/bin/env bash

# Exit script if a statement returns a non-true return value.
set -o errexit

# Use the error status of the first failure, rather than that of the last item in a pipeline.
set -o pipefail

# shellcheck disable=SC1091
source ../registry-config.sh

# shellcheck disable=SC1091
source ../version-tags.sh

for TAG in ${OCI_TAGS[*]}; do
  docker push "${CONTAINER_REGISTRY}/${CONTAINER_REGISTRY_REPO}/horizon:${TAG}"
done
