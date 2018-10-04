# or_guz
Stored Procedure for Or Guz


Instructions
------------ 

This project uses maven, to build a jar-file with the procedure in this
project, simply package the project with maven:

    mvn clean package

This will produce a jar-file, `target/procedures-1.0-SNAPSHOT.jar`,
that can be copied to the `plugin` directory of your Neo4j instance.

    cp target/procedures-1.0-SNAPSHOT.jar neo4j-enterprise-3.4.7/plugins/.
    
  

Restart your Neo4j Server. A new Stored Procedure is available:

    CALL or.guz.finder(from, to) - find connections
    
To try it, create a schema: 

    CREATE CONSTRAINT ON (p:Person) ASSERT p.aid IS UNIQUE;
    CREATE CONSTRAINT ON (p:Org) ASSERT p.aid IS UNIQUE;
    CREATE CONSTRAINT ON (p:Profile) ASSERT p.aid IS UNIQUE;


Load some data:    

    LOAD CSV WITH HEADERS FROM 'file:///nodes_ORG.csv' AS row
    CREATE (n:Org:Profile {aid: row.aid, meta_key: row.meta_key})
    
    LOAD CSV WITH HEADERS FROM 'file:///nodes_PERSON.csv' AS row
    CREATE (n:Person:Profile {aid: row.aid, meta_key: row.meta_key})
          
    LOAD CSV WITH HEADERS FROM 'file:///rel_ORG_ORG_A.csv' AS row
    MATCH (n1:Org {aid: row.from}), (n2:Org {aid: row.to})
    MERGE (n1)-[:A]->(n2)    
    
    LOAD CSV WITH HEADERS FROM 'file:///rel_ORG_ORG_B.csv' AS row
    MATCH (n1:Org {aid: row.from}), (n2:Org {aid: row.to})
    MERGE (n1)-[:B]->(n2)    
    
    LOAD CSV WITH HEADERS FROM 'file:///rel_PERSON_ORG_B.csv' AS row
    MATCH (n1:Person {aid: row.from}), (n2:Org {aid: row.to})
    MERGE (n1)-[:B]->(n2)    
    
    LOAD CSV WITH HEADERS FROM 'file:///rel_PERSON_ORG_C.csv' AS row
    MATCH (n1:Person {aid: row.from}), (n2:Org {aid: row.to})
    MERGE (n1)-[:C]->(n2)        
    
    LOAD CSV WITH HEADERS FROM 'file:///rel_PERSON_PERSON_D.csv' AS row
    MATCH (n1:Person {aid: row.from}), (n2:Person {aid: row.to})
    MERGE (n1)-[:D]->(n2)        
    
    LOAD CSV WITH HEADERS FROM 'file:///rel_PERSON_PERSON_E.csv' AS row
    MATCH (n1:Person {aid: row.from}), (n2:Person {aid: row.to})
    MERGE (n1)-[:E]->(n2)        
    
    LOAD CSV WITH HEADERS FROM 'file:///rel_PERSON_PERSON_F.csv' AS row
    MATCH (n1:Person {aid: row.from}), (n2:Person {aid: row.to})
    MERGE (n1)-[:F]->(n2)  
    
Run the procedure:

    MATCH (p1:Person {aid:'348b2a2'}), (p2:Person {aid:'37e0b07'})
    CALL or.guz.finder(p1, p2) YIELD path
    RETURN path                   
    
    MATCH (p1:Person {aid:'34ad4e6'}), (p2:Person {aid:'37e0b07'})
    CALL or.guz.finder(p1, p2) YIELD path
    RETURN path
    
    MATCH (p1:Person {aid:'3192caa'}), (p2:Person {aid:'3192d70'})
    CALL or.guz.finder(p1, p2) YIELD path
    RETURN path