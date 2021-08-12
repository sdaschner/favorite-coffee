CREATE (u:User {name: 'User'});

// old names
MATCH (u:User), (b:CoffeeBean {name: 'El gato loco'})
MERGE (u)-[:RATED {rating: 3}]-(b);
MATCH (u:User), (b:CoffeeBean {name: 'Saboroso'})
MERGE (u)-[:RATED {rating: 1}]-(b);

// new names
MATCH (u:User), (b:CoffeeBean {name: 'Kayon Mountain Guji G1 Natural'})
MERGE (u)-[:RATED {rating: 3}]-(b);
MATCH (u:User), (b:CoffeeBean {name: 'Estate Kayumas'})
MERGE (u)-[:RATED {rating: 1}]-(b);

// simple recommendation based on liked flavors (no percentages or main flavors)
MATCH (user:User)-[:RATED {rating: 3}]-(:CoffeeBean)-[tastes:TASTES]-(flavor:Flavor)
MATCH (bean)-[:TASTES]-(flavor)
WHERE NOT (user)-[:RATED]-(bean)
RETURN bean;




// extract from rating, mention in content! don't create multiple ratings
MATCH (u:User), (b:CoffeeBean {name: 'Buna'})
MERGE (u)-[:RATED {rating: 3}]-(b)


// sort by flavor percentages, main flavors, & number of likes
MATCH (user:User)-[:RATED {rating: 3}]-(:CoffeeBean)-[tastes:TASTES]-(flavor:Flavor)
WITH user, tastes, flavor, tastes.percentage * count(tastes) as weight
RETURN flavor, sum(weight)

// flavor weights
MATCH (flavor:Flavor)
OPTIONAL MATCH (:User)-[rated:RATED]-(:CoffeeBean)-[tastes:TASTES]-(flavor)
WITH coalesce(rated.rating, 2) - 2 as rating, coalesce(tastes.percentage, 1.0) as percentage, flavor
RETURN flavor, sum(rating * percentage) as weight
ORDER BY weight;


// ranking of all beans
MATCH (flavor:Flavor)
OPTIONAL MATCH (:User)-[rated:RATED]-(:CoffeeBean)-[tastes:TASTES]-(flavor)
WITH coalesce(rated.rating, 2) - 2 as rating, coalesce(tastes.percentage, 1.0) as percentage, flavor
WITH flavor, sum(rating * percentage) as flavorWeight
MATCH (bean:CoffeeBean)-[tastes:TASTES]-(flavor)
OPTIONAL MATCH (bean)-[isFrom:IS_FROM]-(origin:Origin)
OPTIONAL MATCH (user:User)-[r:RATED]-(bean)
WITH bean, isFrom, origin, collect(tastes) as tastes, collect(flavor) as flavors, r, user, sum(flavorWeight * tastes.percentage) as weight
RETURN bean, isFrom, origin, tastes, flavors, r, user
ORDER BY weight DESC, bean.name ASC;


// recommendations
MATCH (user:User)-[:RATED {rating: 3}]-(:CoffeeBean)-[tastes:TASTES]-(flavor:Flavor)
WITH user, flavor, tastes.percentage * count(tastes) as weight
WITH user, flavor, sum(weight) as weight
MATCH (bean)-[tastes:TASTES]-(flavor)
WHERE NOT (user)-[:RATED]-(bean)
WITH bean, sum(tastes.percentage * weight) as weight
RETURN bean.name, weight
ORDER BY weight DESC;


// new, unknown recommendations
MATCH (user:User)-[:RATED {rating: 3}]-(:CoffeeBean)-[tastes:TASTES]-(flavor:Flavor)
WITH collect(flavor) as flavors
MATCH (bean:CoffeeBean)
WHERE none(f in [(bean)-[:TASTES]-(f) | f] where f in flavors)
RETURN bean.name

MATCH (user:User)-[:RATED {rating: 3}]-(:CoffeeBean)-[:TASTES]-(flavor:Flavor)
WITH collect(flavor) as flavors
MATCH (bean:CoffeeBean)-[:TASTES]-(flavor)
WITH bean, flavors, collect(flavor) as beanFlavors
WHERE none(f in flavors where f in beanFlavors)
return bean.name


// new, unknown recommendations, not disliked before
MATCH (user:User)-[:RATED {rating: 3}]-(:CoffeeBean)-[tastes:TASTES]-(flavor:Flavor)
WITH user, collect(flavor) as flavors
MATCH (bean:CoffeeBean)
WHERE NOT (user)-[:RATED]-(bean)
AND none(f in [(bean)-[:TASTES]-(f) | f] where f in flavors)
RETURN bean.name

MATCH (user:User)-[:RATED {rating: 3}]-(:CoffeeBean)-[:TASTES]-(flavor:Flavor)
WITH user, collect(flavor) as flavors
MATCH (bean:CoffeeBean)-[:TASTES]-(flavor)
WITH user, bean, flavors, collect(flavor) as beanFlavors
WHERE NOT (user)-[:RATED]-(bean)
AND none(f in flavors where f in beanFlavors)
return bean.name


