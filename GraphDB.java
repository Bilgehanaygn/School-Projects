import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class GraphDB {


    public Graph graph = new Graph();
    public TST<Vertex> tst = new TST<>();

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    static String normalizeString(String s) {
        // Should match all strings that are not alphabetical
        String regex = "[^a-zA-Z]"/* Replace *//* Code here */;
        return s.replaceAll(regex, "").toLowerCase();
    }

    private void clean() {
        // Remove the vertices with no incoming and outgoing connections from your graph
        /* Code here */

        //remove vertices that there is no edge to them
        for(Map.Entry<Long, ArrayList<Edge>> entry: graph.getAdjacencyList().entrySet()){
            if(entry.getValue().size()==0){
                graph.getVertices().remove(entry.getKey());
                graph.getAdjacencyList().remove(entry.getKey());
            }
        }

    }

    public double distance(Vertex v1, Vertex v2) {
        // Return the euclidean distance between two vertices
        /* Code here */

        return Math.sqrt(Math.pow(v1.getLng()-v2.getLng(),2) + Math.pow(v1.getLat()-v2.getLat(),2));
    }


    public long closest(double lon, double lat) {
        // Returns the closest vertex to the given latitude and longitude values
        /* Code here */

        //graph.getVertexArray
        //iterate over it return id of one with min ((lon-vertex.lon)^2 + (lat-vertex.lat)^2)

        ArrayList<Vertex> verticesList = new ArrayList<>(graph.getVertices().values());
        double min = Math.sqrt(Math.pow(verticesList.get(0).getLng()-lon,2) + Math.pow(verticesList.get(0).getLat()-lat,2));
        long closestLong = verticesList.get(0).getId();
        for(int i=1;i<verticesList.size();i++){
            double currentDistance = Math.sqrt(Math.pow(verticesList.get(i).getLng()-lon,2) + Math.pow(verticesList.get(i).getLat()-lat,2));
            if(currentDistance < min){
                min = currentDistance;
                closestLong = verticesList.get(i).getId();
            }
        }

        return closestLong;
    }

    double lon(long v) {
        // Returns the longitude of the given vertex, v is the vertex id
        /* Code here */
        return graph.getVertices().get(v).getLng();
    }


    double lat(long v) {
        // Returns the latitude of the given vertex, v is the vertex id
        /* Code here */
        return graph.getVertices().get(v).getLat();
    }
}
