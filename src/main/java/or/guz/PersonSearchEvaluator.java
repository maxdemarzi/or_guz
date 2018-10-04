package or.guz;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

import java.util.Iterator;

public class PersonSearchEvaluator implements Evaluator {

    @Override
    public Evaluation evaluate(Path path) {
        // Up to Depth 5
        if (path.length() > 5) return  Evaluation.EXCLUDE_AND_PRUNE;
        // Do not include single node paths
        if (path.length() == 0) return Evaluation.EXCLUDE_AND_CONTINUE;
            // st, spt, sot
        if (path.length() <= 2) return Evaluation.INCLUDE_AND_PRUNE;

        // spot, sopt soot
        if (path.length() == 3) {
            Iterator<Node> nodes = path.nodes().iterator();
            nodes.next();
            boolean isNode2Person = isPerson(nodes.next());
            boolean isNode3Person = isPerson(nodes.next());

            if (isNode2Person && isNode3Person) {
                // exclude sppt
                return Evaluation.EXCLUDE_AND_PRUNE;
            }
            return Evaluation.INCLUDE_AND_PRUNE;
        }

        // sopot, soopt, spoot
        if (path.length() == 4) {
            Iterator<Node> nodes = path.nodes().iterator();
            nodes.next();
            boolean isNode2Person = isPerson(nodes.next());
            boolean isNode3Person = isPerson(nodes.next());
            boolean isNode4Person = isPerson(nodes.next());

            if ((!isNode2Person && isNode3Person && !isNode4Person) ||
                    (!isNode2Person && !isNode3Person && isNode4Person) ||
                    (isNode2Person && !isNode3Person && !isNode4Person)) {
                return Evaluation.INCLUDE_AND_PRUNE;
            }
            return Evaluation.EXCLUDE_AND_PRUNE;
        }
        // sopoot, soopot
        if (path.length() == 5) {
            Iterator<Node> nodes = path.nodes().iterator();
            nodes.next();
            boolean isNode2Person = isPerson(nodes.next());
            boolean isNode3Person = isPerson(nodes.next());
            boolean isNode4Person = isPerson(nodes.next());
            boolean isNode5Person = isPerson(nodes.next());

            if ((!isNode2Person && isNode3Person && !isNode4Person && !isNode5Person) ||
                    (!isNode2Person && !isNode3Person && isNode4Person&& !isNode5Person)) {
                return Evaluation.INCLUDE_AND_PRUNE;
            }
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        return Evaluation.EXCLUDE_AND_PRUNE;
    }

    boolean isPerson(Node node) {
        if (Procedures.people.contains(node.getId())) {
            return true;
        } else if (Procedures.orgs.contains(node.getId())) {
            return false;
        } else {
            if (node.hasLabel(Labels.Person)) {
                Procedures.people.addLong(node.getId());
                return true;
            } else {
                Procedures.orgs.addLong(node.getId());
                return false;
            }
        }
    }
}
