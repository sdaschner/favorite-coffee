// clear data
MATCH (n:CoffeeBean) DETACH DELETE n;
MATCH (n:Flavor) DETACH DELETE n;
MATCH (n:Origin) DETACH DELETE n;
MATCH (n:User) DETACH DELETE n;
CREATE (:User {name: 'User'});

// flavors
UNWIND [
  {flavor: 'Nutty'},
  {flavor: 'Sweet'},
  {flavor: 'Caramel'},
  {flavor: 'Chocolate'},
  {flavor: 'Fruit'},
  {flavor: 'Floral'},
  {flavor: 'Sour'},
  {flavor: 'Green'},
  {flavor: 'Other'},
  {flavor: 'Roasted'},
  {flavor: 'Spices'}
] AS row
CREATE (:Flavor {name: row.flavor});


// countries
UNWIND [
  {country: 'Colombia'},
  {country: 'Ethiopia'},
  {country: 'Kenya'},
  {country: 'Brazil'}
] AS row
CREATE (o:Origin {country: row.country})
;

// beans
UNWIND [
  {name: 'Buna', country: 'Ethiopia', profile: [
    {percentage: 0.4, flavor: 'Fruit'},
    {percentage: 0.3, flavor: 'Sweet'},
    {percentage: 0.3, flavor: 'Caramel'}
  ]},
  {name: 'El gato loco', country: 'Colombia', profile: [
    {percentage: 0.7, flavor: 'Fruit'},
    {percentage: 0.3, flavor: 'Floral'}
  ]},
  {name: 'Saboroso', country: 'Brazil', profile: [
    {percentage: 0.5, flavor: 'Chocolate'},
    {percentage: 0.3, flavor: 'Nutty'},
    {percentage: 0.2, flavor: 'Caramel'}
  ]},
  {name: 'Kahawa Nzuri', country: 'Kenya', profile: [
    {percentage: 0.5, flavor: 'Chocolate'},
    {percentage: 0.5, flavor: 'Caramel'}
  ]}
] AS row
CREATE (b:CoffeeBean {uuid: randomUUID(), name: row.name})
WITH b, row
MATCH (c:Origin {country: row.country})
MERGE (b)-[:IS_FROM]->(c)
WITH b, row
UNWIND row.profile AS profile
MATCH (f:Flavor {name: profile.flavor})
MERGE (b)-[:TASTES {percentage: profile.percentage}]->(f)
;


