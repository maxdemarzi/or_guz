package or.guz;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

import java.util.*;
import java.util.stream.Stream;

import static org.neo4j.graphdb.traversal.Uniqueness.NODE_PATH;

public class Procedures {

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;

    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;

    public static final Roaring64NavigableMap people = new Roaring64NavigableMap();
    public static final Roaring64NavigableMap orgs = new Roaring64NavigableMap();


    static final PersonSearchEvaluator pse = new PersonSearchEvaluator();

    @Procedure(name = "or.guz.finder", mode = Mode.READ)
    @Description("CALL or.guz.finder(from, to) - find connections")
    public Stream<PathResult> personFinder(@Name("from") Node from, @Name("to") Node to) {
        // To make label checking easier preload them here
        if (people.isEmpty()) {
            db.findNodes(Labels.Person).stream().forEach(x -> people.addLong(x.getId()));
        }
        if (orgs.isEmpty()) {
            db.findNodes(Labels.Org).stream().forEach(x -> orgs.addLong(x.getId()));
        }

        TraversalDescription traversalDescription = db.traversalDescription()
                .breadthFirst()
                .evaluator(Evaluators.toDepth(5))
                .uniqueness(NODE_PATH)
                .expand(PathExpanders.allTypesAndDirections());


        BidirectionalTraversalDescription bidirtd = db.bidirectionalTraversalDescription()
                .mirroredSides(traversalDescription)
                .collisionEvaluator(pse);

        return bidirtd.traverse(from, to).iterator().stream().map(PathResult::new);
    }

}
