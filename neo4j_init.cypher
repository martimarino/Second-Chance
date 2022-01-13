/* SCRIPT TO INITIALIZE NEO4J-DB */


/* *** User Information Upload Script ***  */

LOAD CSV WITH HEADERS FROM 'file:///user.csv' AS line
CREATE (:User {username: line['username'], country: line['country']})


/* *** Insertion Information Upload Script ***  */

LOAD CSV WITH HEADERS FROM 'file:///insertion_final.csv' AS line
CREATE (:Insertion {id: line['uniq_id'], category: line['category'], seller:line['seller']})


/* *** Create Random "FOLLOWS" relationships between users *** */

MATCH (u), (p) WHERE size((u) - [:FOLLOWS] -> (p)) < 40
WITH u, p LIMIT 40000 WHERE rand() < 0.8
CREATE (u) - [:FOLLOWS] -> (p)


/* *** Delete self "FOLLOWS" loop *** */

MATCH (u:User) - [r:FOLLOWS] -> (u) 
DELETE rel;


/* *** Create "POSTED" relationships *** */

MATCH (i:Insertion), (u:User)
WHERE i.seller = u.username
CREATE (u) - [r:POSTED] -> (i)
RETURN type(r)


/* *** Create Random "INTERESTED" relationships between users and insertions *** */

WITH range(1,5) as insertionsRange
// next get all insertion in a list
MATCH (i:Insertion)
WITH collect(i) as insertions, insertionsRange
MATCH (u:User)
// randomly pick number of insertions in the range, use that to get a number of random insertions
WITH u, apoc.coll.randomItems(insertions, apoc.coll.randomItem(insertionsRange)) as insertions
// create relationships
FOREACH (insertion in insertions | CREATE (u)-[:INTERESTED]->(insertion))


