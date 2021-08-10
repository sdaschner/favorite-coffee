#!/bin/bash
set -euo pipefail
cd ${0%/*}

docker run --rm \
  --name graph-db \
  -v $(pwd)/deployment/migration/data.csv:/import/data.csv \
  -p 7474:7474 -p 7687:7687 \
  --env NEO4J_AUTH=neo4j/test \
  neo4j:4.0.4
