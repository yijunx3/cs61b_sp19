package bearmaps.proj2c;

import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.KDTree;
import bearmaps.proj2ab.Point;
import bearmaps.lab9.MyTrieSet;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private Map<Point, Long> pointToID;
    private KDTree kdTree;

    private MyTrieSet Trie;
    private Map<String, List<Node>> cleanedNameToNodes;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        // List<Node> nodes = this.getNodes();
        pointToID = new HashMap<>();
        List<Point> points = new ArrayList<>();
        List<Node> nodes = this.getNodes();

        Trie = new MyTrieSet();
        cleanedNameToNodes = new HashMap<>();

        for (Node node : nodes) {
            long id = node.id();

            // If the node has a name, clean it, then add it to the Trie,
            // and put the (cleaned name, full name) pair into the cleanedToFull map.
            if (this.name(id) != null) {
                String fullName = this.name(id);
                String cleanedName = cleanString(fullName);

                Trie.add(cleanedName);
                if (!cleanedNameToNodes.containsKey(cleanedName)) {
                    cleanedNameToNodes.put(cleanedName, new LinkedList<>());
                }
                cleanedNameToNodes.get(cleanedName).add(node);
            }

            // Only consider the node that has neighbors,
            // and turn these nodes to Points to service
            // the KDTree.
            if (!this.neighbors(id).isEmpty()) {
                double x = node.lon();
                double y = node.lat();
                Point point = new Point(x, y);

                points.add(point);
                pointToID.put(point, id);
            }
        }

        kdTree = new KDTree(points);
    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        Point closestPoint = kdTree.nearest(lon, lat);
        return pointToID.get(closestPoint);
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with or without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        String cleanedPrefix = cleanString(prefix);
        List<String> matchedNames = Trie.keysWithPrefix(cleanedPrefix);
        List<String> locations = new LinkedList<>();

        for (String name : matchedNames) {
            for (Node node : cleanedNameToNodes.get(name)) {
                if (!locations.contains(node.name())) {
                    locations.add(node.name());
                }
            }
        }

        return locations;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> locations = new LinkedList<>();
        String cleanedLocationName = cleanString(locationName);

        // Return an empty list if no location name matches the locationName.
        if (cleanedNameToNodes.containsKey(cleanedLocationName)) {
            for (Node node : cleanedNameToNodes.get(cleanedLocationName)) {
                Map<String, Object> locationInfo = new HashMap<>();
                locationInfo.put("id", node.id());
                locationInfo.put("name", node.name());
                locationInfo.put("lon", node.lon());
                locationInfo.put("lat", node.lat());
                locations.add(locationInfo);
            }
        }

        return locations;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
