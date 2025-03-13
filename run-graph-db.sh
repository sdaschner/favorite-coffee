#!/bin/bash
set -euo pipefail
cd ${0%/*}

trap cleanup EXIT

function cleanup() {
  docker stop graph-db &> /dev/null || true
}


docker run -d --rm \
  --name graph-db \
  -v $(pwd)/deployment/migration:/import \
  -p 7474:7474 -p 7687:7687 \
  --env NEO4J_AUTH=neo4j/test "$@" \
  neo4j:4.4.12

echo waiting for Neo4j...
wget --quiet --tries=30 --waitretry=2 --retry-connrefused -O /dev/null http://localhost:7474
echo adding data...

docker exec graph-db /var/lib/neo4j/bin/cypher-shell -u neo4j -p test --file /import/load-coffee-data.cypher
#docker exec graph-db /var/lib/neo4j/bin/cypher-shell -u neo4j -p test --file /import/example-data.cypher

docker logs -f graph-db
