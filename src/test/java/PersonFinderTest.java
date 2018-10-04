import or.guz.Procedures;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static junit.framework.TestCase.assertEquals;

public class PersonFinderTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public final Neo4jRule neo4j = new Neo4jRule()
            .withFixture(MODEL_STATEMENT)
            .withProcedure(Procedures.class);

    @Test
    public void testTraversal() throws Exception {
        Procedures.people.clear();
        Procedures.orgs.clear();

        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/db/data/transaction/commit").toString(), QUERY1);
        int count = response.get("results").get(0).get("data").size();
        assertEquals(1, count);
    }

    private static final Map QUERY1 =
            singletonMap("statements", singletonList(singletonMap("statement",
                    "MATCH (p1:Person {aid:'1'}), (p2:Person {aid:'2'}) " +
                    "CALL or.guz.finder(p1, p2) YIELD path " +
                    "RETURN path")));

    private static final String MODEL_STATEMENT =
            "CREATE (p1:Person { aid: '1' })" +
            "CREATE (p2:Person { aid: '2' })" +
            "CREATE (p1)-[:A]->(p2)";
}
