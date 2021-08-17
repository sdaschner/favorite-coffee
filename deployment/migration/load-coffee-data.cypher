// clear data
MATCH (n:CoffeeBean) DETACH DELETE n;
MATCH (n:Flavor) DETACH DELETE n;
MATCH (n:User) DETACH DELETE n;
CREATE (:User {name: 'User'});

CREATE CONSTRAINT IF NOT EXISTS ON (n:CoffeeBean) ASSERT n.uuid IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS ON (n:Flavor) ASSERT n.name IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS ON (n:Origin) ASSERT n.country IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS ON (n:User) ASSERT n.name IS UNIQUE;

// example:
//   Name,Origin,Flavors
//   Blue Moon,Indonesia,Spicy:0.28571428571428575;Chocolaty:0.4285714285714286;Fruity:0.14285714285714288;Flowery:0.14285714285714288

LOAD CSV WITH HEADERS FROM 'file:///data.csv' AS row
//LOAD CSV WITH HEADERS FROM 'https://raw.githubusercontent.com/sdaschner/favorite-coffee/main/deployment/migration/data.csv' AS row
MERGE (b:CoffeeBean {uuid: randomUUID(), name: row.Name})
MERGE (o:Origin {country: row.Origin})
MERGE (b)-[:IS_FROM]->(o)
WITH row, b, o
UNWIND split(row.Flavors, ';') AS flavor
WITH b, split(flavor, ':') AS parts
MERGE (f:Flavor {name: toString(parts[0])})
MERGE (b)-[:TASTES {percentage: toFloat(parts[1])}]->(f)
;

MATCH () RETURN count(*) as nodes;
MATCH ()-->() RETURN count(*) as relationships;
