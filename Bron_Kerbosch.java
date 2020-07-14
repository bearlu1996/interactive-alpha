

/*The code in this file is modified directly from  https://github.com/lizi-git/Bron-Kerbosch/blob/master/Bron-Kerbosch.java*/

package org.processmining.alphaminer.abstractions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @param <Node> vertex class of graph
 * @param <E> edge class of graph
 */
public class Bron_Kerbosch
{
    //~ Instance fields --------------------------------------------------------

    private final Graph graph;

    private Collection<Set<Node>> cliques;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new clique finder. Make sure this is a simple graph.
     *
     * @param graph the graph in which cliques are to be found; graph must be
     * simple
     */
    public Bron_Kerbosch(Graph graph)
    {

        this.graph = graph;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Finds all maximal cliques of the graph. A clique is maximal if it is
     * impossible to enlarge it by adding another vertex from the graph. Note
     * that a maximal clique is not necessarily the biggest clique in the graph.
     *
     * @return Collection of cliques (each of which is represented as a Set of
     * vertices)
     */
    public Collection<Set<Node>> getAllMaximalCliques()
    {
        // TODO:  assert that graph is simple

        cliques = new ArrayList<>();
        List<Node> potential_clique = new ArrayList<>();
        List<Node> candidates = new ArrayList<>();
        List<Node> already_found = new ArrayList<Node>();
        candidates.addAll(graph.nodes);
       
        findCliques(potential_clique, candidates, already_found);
        return cliques;
    }

    /**
     * Finds the biggest maximal cliques of the graph.
     *
     * @return Collection of cliques (each of which is represented as a Set of
     * vertices)
     */
    public Collection<Set<Node>> getBiggestMaximalCliques()
    {
        // first, find all cliques
        getAllMaximalCliques();

        int maximum = 0;
        Collection<Set<Node>> biggest_cliques = new ArrayList<Set<Node>>();
        for (Set<Node> clique : cliques) {
            if (maximum < clique.size()) {
                maximum = clique.size();
            }
        }
        for (Set<Node> clique : cliques) {
            if (maximum == clique.size()) {
                biggest_cliques.add(clique);
            }
        }
        return biggest_cliques;
    }

    private void findCliques(
        List<Node> potential_clique,
        List<Node> candidates,
        List<Node> already_found)
    {
        List<Node> candidates_array = new ArrayList<Node>(candidates);
        if (!end(candidates, already_found)) {
            // for each candidate_node in candidates do
            for (Node candidate : candidates_array) {
                List<Node> new_candidates = new ArrayList<Node>();
                List<Node> new_already_found = new ArrayList<Node>();

                // move candidate node to potential_clique
                potential_clique.add(candidate);
                candidates.remove(candidate);

                // create new_candidates by removing nodes in candidates not
                // connected to candidate node
                for (Node new_candidate : candidates) {
                    if (candidate.connections.contains(new_candidate))
                    {
                        new_candidates.add(new_candidate);
                    } // of if
                } // of for

                // create new_already_found by removing nodes in already_found
                // not connected to candidate node
                for (Node new_found : already_found) {
                    if (candidate.connections.contains(new_found)) {
                        new_already_found.add(new_found);
                    } // of if
                } // of for

                // if new_candidates and new_already_found are empty
                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
                    // potential_clique is maximal_clique
                    cliques.add(new HashSet<Node>(potential_clique));
                } // of if
                else {
                    // recursive call
                    findCliques(
                        potential_clique,
                        new_candidates,
                        new_already_found);
                } // of else

                // move candidate_node from potential_clique to already_found;
                already_found.add(candidate);
                potential_clique.remove(candidate);
            } // of for
        } // of if
    }

    private boolean end(List<Node> candidates, List<Node> already_found)
    {
        // if a node in already_found is connected to all nodes in candidates
        boolean end = false;
        int edgecounter;
        for (Node found : already_found) {
            edgecounter = 0;
            for (Node candidate : candidates) {
                if (found.connections.contains(candidate)) {
                    edgecounter++;
                } // of if
            } // of for
            if (edgecounter == candidates.size()) {
                end = true;
            }
        } // of for
        return end;
    }
}
