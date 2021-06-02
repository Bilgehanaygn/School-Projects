import java.util.ArrayList;
import java.util.HashMap;


public class Graph {
    // Implement the graph data structure here
    // Use Edge and Vertex classes as you see fit
    /* Code here */
    private HashMap<Long, Vertex> vertices;
    private HashMap<Long, ArrayList<Edge>> adjacencyList;
    private Long lastVertex;

    public Graph(){
        this.vertices = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.lastVertex = null;
    }

    public HashMap<Long, Vertex> getVertices(){
        return this.vertices;
    }

    public HashMap<Long, ArrayList<Edge>> getAdjacencyList(){
        return this.adjacencyList;
    }

    public void addVertex(Vertex vertex){
        vertices.put(vertex.getId(),vertex);
        adjacencyList.put(vertex.getId(), new ArrayList<>());
        lastVertex = vertex.getId();
    }

    public void addEdge(Edge edge){
        adjacencyList.get(edge.getSource().getId()).add(edge);
    }

    public Long getLastVertex(){
        return lastVertex;
    }




}
