import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Creates a graph of vertices, which hold String data, and Edges, which connect vertices.
 * Each vertex is stored in a tree map, and each edge is stored in a linked list.
 * A portion of the graph is considered connected if each vertex in the subset can be connected through edges to every other vertex in the subset.
 * A connected portion of the graph can be stored in a linked list which holds every vertex connected to a central vertex.
 * 
 * A Path object can be made to represent the shortest path between a central vertex and a destination vertex.
 * 
 * "Data Structures and Problem Solving using Java" by Mark Allen Weiss was referenced for the Dijkstra's algorithm implementation,
 * as well as for some small design concepts (such as the path object).
 * Graphs are cool.
 * @author Emily Rizzo
 * @author Mark Allen Weiss
 */
public class MyGraph {
	LinkedList<String> actors;
	HashMap<String, Vertex> vertices;
	LinkedList<Edge> edges;
	int numVertex;
	int numEdges;
	int INF;
	int reachable;
	int unreachable;
	LinkedList<Vertex> connected;
	LinkedList<Entry> avgs;

	/**
	 * Creates a directed Edge on the graph. Edge contains the vertex it originates from, the vertex
	 * it maps to, and the cost of the traversal. 
	 * @author Emily Rizzo
	 */
	public class Edge{
		Vertex u;
		Vertex v;
		int cost;

		/**
		 * creates a directed edge from u to v, with cost cost
		 * @param u - origin of edge
		 * @param v - destination of edge
		 * @param cost - value associated with traversal from u to v
		 */
		public Edge(Vertex u, Vertex v, int cost) {
			this.u = u;
			this.v= v;
			this.cost = cost;
			INF = INF + cost;
		}

		/**
		 * returns the destination of the edge
		 * @return - destination vertex of the edge, v
		 */
		public Vertex to() {
			return this.v;
		}

		/**
		 * returns the origin of the edge
		 * @return - origin vertex of the edge, u
		 */
		public Vertex from() {
			return this.u;
		}

		/**
		 * returns the cost of the edge
		 * @return - integer value associated with traversal from u to v
		 */
		public int cost() {
			return this.cost;
		}

		/**
		 * Resets cost to be c
		 */
		public void newCost(int c){
			this.cost = c;
		}
		/**
		 * hashCode is determined by destination hashCode.
		 */
		public int hashCode() {
			return this.v.hashCode();
		}

		/**
		 * returns edge in format ( u, v, cost )
		 * @return String representation of edge, ( u, v, cost )
		 */
		public String toString() {
			return "( " + this.u.data() + ", " + this.v.data() + ", " + this.cost + " )";
		}
	}

	/**
	 * Creates a vertex on the graph. Vertex contains a data object,
	 * a hashmap of all incoming edges, and a hashmap of all outgoing edges.
	 * @author Emily Rizzo
	 *
	 */
	public class Vertex{
		String data;
		HashMap<String ,Edge> out;
		HashMap<String ,Edge> in;
		LinkedList<Edge> outEdges;
		public boolean check;
		public int path;
		public Vertex prev;

		/**
		 * creates a vertex object. Initializes data to data, then creates 2 empty hashmaps for the
		 * inbound and outbound edges.
		 * @param data
		 */
		public Vertex(String data) {
			this.data = data;
			this.out = new HashMap<String ,Edge>();
			this.in = new HashMap<String ,Edge>();
			this.outEdges = new LinkedList<Edge>();
		}

		/**
		 * returns the data stored in the vertex
		 * @return - this.data, data stored in this vertex
		 */
		public String data() {
			return this.data;
		}

		/**
		 * hashcode is determined by the data's hashcode.
		 */
		public int hashCode() {
			return this.data.hashCode();
		}

		/**
		 * Returns string representation of the vertex, which is the data's representation.
		 * @return - data.toString, the data's string representation.
		 */
		public String toString() {
			return this.data;
		}

		/**
		 * adds a directed edge to the current vertex. Adds the edge to the inbound or outbound edge Hashmap,
		 * depending on the direction of the edge.
		 * @param in - true if the new edge is inbound, false if the new edge is outbound.
		 * @param v - vertex with which the new edge is being created.
		 * @param cost - cost of the new edge being created.
		 */
		public Edge add(boolean in, Vertex v, int cost) {
			if (in) {
				Edge edge = new Edge(v, this, cost);
				this.in.put(v.data, edge);
				v.out.put(this.data, edge);
				return edge;
			} else {
				Edge edge = new Edge(this, v, cost);
				this.out.put(v.data, edge);
				v.in.put(this.data, edge);
				this.outEdges.add(edge);
				return edge;
			}
		}

		/**
		 * Returns true if the vertex v is adjacent to the current vertex. Returns true even if there is only one
		 * directed edge either into or from the current vertex.
		 * @param v - vertex to find adjacency for
		 * @return - true if at least one edge connects vertex v and the current vertex. False if no such edge exists.
		 */
		public boolean isAdjacent(Vertex v) {
			if (this.out.containsKey(v.data) || this.in.containsKey(v.data)) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * removes the edge e from BOTH the inbound and outbound edges list, and returns true. Returns false if the
		 * edge is not in the inbound or outbound edge list.
		 * @param e - edge to be removed
		 * @return true if edge is removed, false if it was not present in the graph.
		 */
		public boolean removeEdge(Edge e) {
			if (in.containsKey(e.from().data) || out.containsKey(e.to().data)){
				if (in.containsKey(e.from().data)) {
					e.from().out.remove(e.from().data);
				} if (out.containsKey(e.to().data)) {
					e.to().in.remove(e.to().data);
				}
				in.remove(e.from().data);
				out.remove(e.to().data);
				outEdges.remove(e);
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Removes all inbound and outbound edges from the current vertex.
		 * Also removes all edges connecting to the current vertex from all adjacent vertexes
		 */
		public void clear() {
			Iterator<Edge> inbound = in.values().iterator();
			Iterator<Edge> outbound = out.values().iterator();
			while (inbound.hasNext()) {
				Edge i = inbound.next();
				i.from().removeEdge(i);
			}
			while (outbound.hasNext()) {
				Edge o = outbound.next();
				o.to().removeEdge(o);
			}
			in.clear();
			out.clear();
			outEdges.clear();
		}

		/**
		 * @return - linked list of outbound edges.
		 */
		public LinkedList<Edge> getEdges(){
			return this.outEdges;
		}

		/**
		 * resets vertex. Unmarks the vertex, sets the path cost to be INF, and sets prev to null
		 */
		public void reset() {
			this.check = false;
			this.path = INF;
			this.prev = null;
		}

	}


	public MyGraph() {
		actors = new LinkedList<String>();
		vertices = new HashMap<String,Vertex>();
		edges = new LinkedList<Edge>();
		numVertex = 0;
		numEdges = 0;
		INF = 0;
		reachable = 0;
		unreachable = 0;
		connected = new LinkedList<Vertex>();
		avgs = new LinkedList<Entry>();
	}

	/**
	 * @return - number of vertices in the graph
	 */
	public int vertices() {
		return this.numVertex;
	}

	/**
	 * @return - number of edges in the graph
	 */
	public int edges() {
		return this.numEdges;
	}

	/**
	 * returns true if v1 and v2 are adjacent. returns true even if there is only one directed edge from v1 to v2 or vice versa.
	 * @param d1 - Vertex 1 (string data)
	 * @param d2 - Vertex 2 (string data)
	 * @return - true if an edge exists linking v1 and v2 in either direction. returns false if no such edge exists.
	 */
	public boolean areAdjacent(String d1, String d2) {
		Vertex v1 = getVertex(d1);
		Vertex v2 = getVertex(d2);
		return v1.isAdjacent(v2);
	}

	/**
	 * Removes an edge if it exists in the graph. Returns true if the edge was removed, returns false if it did not exist.
	 * @param e - edge to be removed
	 * @return - true if edge was removed, false if it did not exist in the graph.
	 */
	public boolean removeEdge(String d1, String d2) {
		Edge e = this.getEdge(d1,d2);
		if (e != null) {
			e.from().removeEdge(e);
			e.to().removeEdge(e);
			this.edges.remove(e);
			this.numEdges--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes the vertex v from the graph. Deletes all edges connecting v to the graph and removes it from the vertex list.
	 * @param v - vertex to be removed
	 * @return - true if v was removed, false if v was not in the graph.
	 */
	public boolean removeVertex(String d) {

		Vertex v = getVertex(d);
		LinkedList<Edge> e = v.getEdges();
		int num = e.size();
		if (this.vertices.containsKey(d)) {
			v.clear();
			this.vertices.remove(d);
			this.numVertex--;
			this.numEdges = this.numEdges - num;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Inserts a directed edge from v1 to v2, with cost cost. Adds the new edge to the edge list, increments number of edges.
	 * @param v1 - origin vertex of the new edge
	 * @param v2 - destination vertex of the new edge
	 * @param cost - cost associated with the new edge.
	 */
	public Edge insertEdge(String data1, String data2, int cost) {
		Vertex v1 = getVertex(data1);
		Vertex v2 = getVertex(data2);
		if (v1 != null && v2 != null) {
			if (v1.isAdjacent(v2)){
				Edge e = this.getEdge(v1, v2);
				if (e != null){
					e.newCost(cost);
					return null;
				}
			}
			Edge edge = v1.add(false, v2, cost);
			this.edges.add(edge);
			this.numEdges++;
			return edge;
		}
		return null;
	}

	/**
	 * Inserts a vertex into the graph. Adds the vertex to the vertices list, increments number of vertices.
	 * @param v - vertex to be added to the graph.
	 */
	public Vertex insertVertex(String data, boolean actor) {
		if (!this.vertices.containsKey(data)) {
			Vertex v = new Vertex(data);
			this.vertices.put(data, v);
			this.numVertex++;
			if (actor) {
				this.actors.add(data);
			}
			return v;
		}
		return null;
	}

	public Vertex insertVertex(String data) {
		return this.insertVertex(data, false);
	}

	/**
	 * returns a list of vertices adjacent to the current vertex. Returns all vertices which share an edge with
	 * v, inbound or outbound.
	 * @param v - vertex to find adjacent vertices to
	 * @return - linked list of all vertices which share an edge with the vertex v.
	 */
	public LinkedList<Vertex> adjacentVertices(String d){
		Vertex v = getVertex(d);
		LinkedList<Vertex> adjacent = new LinkedList<Vertex>();
		if (this.vertices.containsKey(d)) {
			Iterator<Edge> inbound = v.in.values().iterator();
			Iterator<Edge> outbound = v.out.values().iterator();
			while(inbound.hasNext()) {
				adjacent.add(inbound.next().from());
			}
			while (outbound.hasNext()) {
				adjacent.add(outbound.next().to());
			}
		}
		return adjacent;
	}

	/**
	 * Returns a linked list of all edges incident to the vertex v. Includes both inbound and outbound edges.
	 * @param v - vertex to find incident edges to
	 * @return linked list of all edges incident to vertex v
	 */
	public LinkedList<Edge> incidentEdges(Vertex v){
		String data = v.data();
		LinkedList<Edge> incident = new LinkedList<Edge>();
		if (this.vertices.containsKey(data)) {
			Iterator<Edge> inbound = v.in.values().iterator();
			Iterator<Edge> outbound = v.out.values().iterator();
			while(inbound.hasNext()) {
				incident.add(inbound.next());
			}
			while (outbound.hasNext()) {
				incident.add(outbound.next());
			}
		}
		return incident;
	}

	/**
	 * Returns the vertex associated with data
	 * @param data - data of the expected vertex
	 * @return - vertex object in graph which contains data. Returns null if vertex is not in graph.
	 */
	public Vertex getVertex(String data) {
		Vertex v = this.vertices.get(data);;
		if (v == null){
			data = data + " (I)";
			v = this.vertices.get(data);
		}
		return v;
	}

	/**
	 * Returns the edge from v1 to v2, if it exists in the graph.
	 * @param v1 - origin of the edge to be found
	 * @param v2 - destination of the edge to be found
	 * @return - edge from v1 to v2
	 */
	public Edge getEdge(Vertex v1, Vertex v2) {
		String to = v2.data();
		return (v1.out.get(to));
	}

	/**
	 * Runs getEdge with the String representations of 2 vertices d1 and d2.
	 * @param d1 - data of vertex 1
	 * @param d2 - data of vertex 2
	 * @return - edge from vertex 1 to vertex 2
	 */
	public Edge getEdge(String d1, String d2) {
		Vertex v1 = this.getVertex(d1);
		Vertex v2 = this.getVertex(d2);
		return this.getEdge(v1,v2);
	}

	/**
	 * converts the graph to a string of format: 
	 * Vertex 1: (vertex data) || (v1edge1), (v1edge2), ... (v1edge x)
	 * Vertex 2: (vertex data || (v2edge1), (v2edge2), ... (v2edge x)
	 * .
	 * .
	 * .
	 * Vertex n: (vertex data) ||(vn edge1), (vn edge2), ... (vn edge x)
	 * 
	 * for a graph of n vertices, where each vertex has x edges.
	 * includes inbound and outbound edges.
	 * 
	 */
	public String toString() {
		String graph = "";
		Iterator<String> itr = vertices.keySet().iterator();
		int i = 0;
		while (itr.hasNext()) {
			//	for (int i = 0; i < this.vertices.size(); i++) {
			Vertex v = this.vertices.get(itr.next());
			graph = graph + "Vertex " + i + ": " + v + " || Edges: ";
			LinkedList<Edge> e = incidentEdges(v);
			for(int j = 0; j < e.size(); j++) {
				graph = graph + e.get(j).toString();
			}
			graph = graph + "\n";
			i++;
		}
		return graph;
	}

	/**
	 * Returns a string with all distances in the graph, starting from vertex s. Only useful for testing purposes.
	 * @param s - central vertex
	 * @return - String representation of distances
	 */
	public String getDists(String s){
		String table = "";
		Vertex start = getVertex(s);
		HashMap<Vertex,Path> dist = dijkstra(start);
		table = "Distances from Vertex " + start + ":" + "\n";
		Iterator<String> itr = vertices.keySet().iterator();
		while (itr.hasNext()) {
			Vertex v = vertices.get(itr.next());
			table = table +  v.toString() + " || " + dist.get(v).cost + " (" + v;
			Vertex cpath = v;
			while (cpath.prev != null) {
				table = table  + " <- "+ cpath.prev;
				cpath = cpath.prev;
			}
			table = table + ")" + "\n";
		}
		return table;

	}

	/**
	 * resets all vertices in the graph. Removes prev values and sets distances to INF, but 
	 * maintains all vertices and edges in the graph.
	 */
	private void resetAll() {
		Iterator<String> itr = vertices.keySet().iterator();
		while (itr.hasNext()){
			Vertex v = getVertex(itr.next());
			if (v != null) {
				v.reset();
			}
		}
	}

	/**
	 * Runs Dijkstra's algorithm on the graph, starting from vertex start. 
	 * Returns a HashMap with each vertex in the graph and a path from start to that vertex.
	 * @param start - central vertex which the algorithm runs from
	 * @return - HashMap with each vertex and a path from start to the vertex
	 */
	public HashMap<Vertex,Path> dijkstra(Vertex start) {
		String center = start.data();
		HashMap<Vertex, Path> distances = new HashMap<Vertex, Path>();
		PriorityQueue<Path> queue = new PriorityQueue<Path>();
		if (!this.vertices.containsKey(center)){
			throw new NoSuchElementException("Start vertex doesn't exist in graph.");
		}
		resetAll();
		start.path = 0;
		start.prev = null;
		Path x = new Path(start, 0);
		queue.add(x);
		distances.put(start, x);
		int count = 0;
		while(!queue.isEmpty() && count < vertices.size()) {
			Path p = queue.poll();
			Vertex u = p.dest;
			if (u.check) {
				continue;
			}
			u.check = true;
			count++;
			LinkedList<Edge> edges = u.getEdges();
			for (int i = 0; i < edges.size(); i++) {
				Edge e = edges.get(i);
				Vertex v = e.to();
				int costUV = e.cost();

				if (costUV < 0) {
					throw new IndexOutOfBoundsException("Negative edges are not approved");
				}
				if (v.path > u.path + costUV) {
					v.path = u.path + costUV;
					v.prev = u;
					Path next = new Path(v, v.path);
					distances.put(v, next);
					queue.add(next);
				}
			}
		}
		return distances;
	}

	/**
	 * Returns the path from the center to the vertex associated with name. Path is put in the format of: 
	 * name -> movie1 -> intermediate name -> movie2 -> .... movie x -> center (bacon number)
	 * @param hm - hash map generated by Dijkstra algorithm for current center
	 * @param name - actor to find a path to
	 * @return - String representation of the path.
	 */
	public String find(HashMap<Vertex,Path> hm, String name){
		MyGraph.Path path = this.findPath(hm, name);
		String p = name;
		if (path != null){
			MyGraph.Vertex prev = path.dest;
			//System.out.println(prev);
			while (prev.prev != null){
				//System.out.println(prev.prev);
				p = p + " -> " + prev.prev;
				prev = prev.prev;
			}
			p = p + " (" + path.cost + ")";
		} else {
			p = p + " is unreachable";
		}
		return p;
	}

	/**
	 * Finds path from current center to vertex associated with data.
	 * @param hm - hash map for the current center
	 * @param data - vertex to find a path to
	 * @return - Path object associated with the vertex to be found
	 */
	public Path findPath(HashMap<Vertex,Path> hm, String data) {
		Vertex v = null;
		v = this.getVertex(data);
		return hm.get(v);
	}

	/**
	 * Loads the connected LinkedList given a hash map generated by the Dijkstra algorithm.
	 * An actor is added to the connected list if the actor can be connected to the current center.
	 * Also updates reachable and unreachable.
	 * @param hm - hash map generated by Dijkstra algorithm based on current center.
	 */
	public void loadConnected(HashMap<Vertex, Path> hm) {
		//		this.connected = new LinkedList<Vertex>();
		if (this.connected.size() == 0){
			this.unreachable = 0;
			this.reachable = 0;
			Integer[] counts = new Integer[10];
			for (int i = 0; i < counts.length; i++) {
				counts[i] = 0;
			}
			Iterator<Vertex> itr = hm.keySet().iterator();
			while (itr.hasNext()) {
				Vertex v = itr.next();
				Path p = hm.get(v);
				if (this.actors.contains(p.dest.data())) {
					this.reachable++;
					this.connected.add(p.dest);
				}
			}
			unreachable = this.actors() - reachable;
		}
	}

	/**
	 * Calculates the average distance from the current center to any other actor in the graph using a hash map generated by the Dijkstra algorithm.
	 * retrieves distance for each actor, then divides by number of actors.
	 * Calculation does not include the center, or any unreachable actors.
	 * @param hm - hash map of actors and their path length, constructed with the current center
	 * @return
	 */
	public float avgDist(HashMap<Vertex,Path> hm) {
		float total = (float) 0.0;
		Iterator<Vertex> itr = connected.iterator();
		while(itr.hasNext()) {
			Vertex v = itr.next();
			Path p = hm.get(v);
			if (p != null) {
				if (p.cost != 0 && p.cost != INF) {
					total = total + (float) p.cost;
				}
			}
		}
		return total/connected.size();
	}

	/**
	 * returns the number of actors in the database. Actors are vertices whose actor variable is true.
	 * @return - number of actors in the current graph.
	 */
	public int actors() {
		return this.actors.size();
	}

	/**
	 * returns the number of reachable actors, i.e. actors connected to the current center
	 * @return - number of reachable actors
	 */
	public int reachable() {
		return this.reachable;
	}

	/**
	 * returns the number of unreachable actors, i.e. actors who cannot be connected to the current center.
	 * @return - number of unreachable actors
	 */
	public int unreachable() {
		return this.unreachable;
	}

	/**
	 * clears the current graph
	 */
	public void clear(){
		this.vertices = new HashMap<String, Vertex>();
		this.edges = new LinkedList<Edge>();
		this.numVertex = 0;
		this.numEdges = 0;
		this.INF = 0;
		this.actors = new LinkedList<String>();
		this.connected = new LinkedList<Vertex>();
	}
	
	public void readIn(String source){ readIn(source, 0);}

	/**
	 * reads in database from provided source.
	 * @param source - URL or text file to be added in from
	 */
	public void readIn(String source, int num){
		this.clear();
		Scanner s = null;
		if (source.substring(0, 5).equals("http:")){
			try {
				s = new Scanner( new URL(source).openStream() );
			} catch (MalformedURLException e) {
				System.out.println("Malformed URL or sumthin");
			} catch (IOException e) {
				System.out.println("URL not found or sumthin");
			}
		} else{
			try {
				s = new Scanner(new File(source));
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			}
		}
		if (num == 0){		//if num = 0, number of lines in file is unknown
			System.out.println("Counting number of entries...");
			while (s.hasNextLine()) {
				s.nextLine();
				num++;
			}
		}

		if (source.substring(0, 5).equals("http:")){
			try {
				s = new Scanner( new URL(source).openStream() );
			} catch (MalformedURLException e) {
				System.out.println("Malformed URL or sumthin");
			} catch (IOException e) {
				System.out.println("URL not found or sumthin");
			}
		} else{
			try {
				s = new Scanner(new File(source));
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			}
		}

		int count = 0;
		int ratio = num/45;
		int numBars = 0;
		if (ratio < 1) {
			ratio = 1;
			numBars = 40/num;
		}
		while (s.hasNextLine()) {
			if (count % ratio == 0) {
				numBars++;
			}
			printBar(numBars, count, num);
			String line = s.nextLine();
			String[] array = line.split("\\|");
			String name = array[0];
			String movie = array[1];
			this.insertVertex(name, true);
			this.insertVertex(movie);
			this.insertEdge(name, movie, 0);
			this.insertEdge(movie, name, 1);
			count++;
		}
		System.out.printf("%-78s", "Successfully logged " + count + " entries.");
	}

	/**
	 * A comparable Path class used to find the path to a destination vertex. 
	 * Stores the destination vertex and the cost of the entire path.
	 * Comparing paths yields the comparison between the costs of each path.
	 * 
	 * Textbook referenced for this implementation.
	 * @author Emily Rizzo
	 * @author Mark Allen Weiss
	 */
	public class Path implements Comparable<Path>{
		public Vertex dest;
		public Integer cost;

		public Path(Vertex d, Integer c) {
			this.dest = d;
			this.cost = c;
		}

		public int compareTo(Path rhs) {
			return this.cost.compareTo(rhs.cost);
		}
	}


	/**
	 * Comparable Entry class is used to pair a vertex with its average distance, for the avgdist command.
	 * Made comparable to easily retrieve lowest averages from priority queue.
	 * @author Emily Rizzo
	 */
	public class Entry implements Comparable<Entry>{
		public Vertex v;
		public Float avg;

		/**
		 * makes an entry object which stores the vertex v with average avg
		 * @param v - center vertex 
		 * @param avg - average bacon number of v
		 */
		public Entry(Vertex v, Float avg) {
			this.v = v;
			this.avg = avg;
		}

		/**
		 * returns comparison between 2 averages
		 */
		@Override
		public int compareTo(Entry o) {
			return avg.compareTo(o.avg);
		}

		/**
		 * returns String representation of entry object, in the format:
		 * (average)	(vertex)
		 */
		public String toString() {
			return this.avg  + "   "+ "\t" + this.v;
		}

	}

	/**
	 * prints a list of the top n center vertices in the connected portion of the map containing the initial center.
	 * Top centers are determined by having the lowest average bacon numbers.
	 * @param n - number of top centers to find
	 * @param hm - hashmap produced by Dijkstra's algorithm by the initial center.
	 */
	public void topcenter(int n, HashMap<Vertex, Path> hm) {
		int ratio = this.reachable()/45;
		int numBars = 0;
		if (ratio < 1) {
			ratio = 1;
			numBars = 45/this.reachable();
		}
		if (avgs.size() < n){		//if you have already populated avgs, no need to reprocess actors
			System.out.println("Processing " + connected.size() + " actors...");
			PriorityQueue<Entry> avgsqueue = new PriorityQueue<Entry>();		//place entries into priorityqueue to order them
			for (int i = 0; i < connected.size(); i++) {

				if (i % ratio == 0) {
					numBars++;
				}
				printBar(numBars, i+1, connected.size());
				Vertex v = connected.get(i);
				hm = dijkstra(v);
				Float avg = this.avgDist(hm);
				avgsqueue.add(new Entry(v, avg));
			}
			Entry e = avgsqueue.poll();
			while (e != null){		//fill avgs with the ordered entries from priority queue
				avgs.add(e);
				e = avgsqueue.poll();
			}
			System.out.printf("%-78s", "Successfully processed " + connected.size() + " actors.");
			System.out.println();
		}
		for (int i = 0; i < n; i++) {
			System.out.println(avgs.get(i));
		}
	}

	/**
	 * Creates a table of the counts of bacon numbers for the given center from 0 up to the longest.
	 * @param hm - hashmap generated by Dijkstra's algorithm for the given center.
	 */
	public Integer[] table(HashMap<Vertex, Path> hm) {
		this.unreachable = 0;
		this.reachable = 0;
		Integer[] counts = new Integer[10];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}
		Iterator<Vertex> itr = hm.keySet().iterator();
		while (itr.hasNext()) {
			Vertex v = itr.next();
			Path p = hm.get(v);
			if (this.actors.contains(p.dest.data())) {
				if (p.cost == 0) {
					System.out.println("Table for: " + p.dest);
				}
				if (p.cost < counts.length) {
					counts[p.cost]++;
				} else if(p.cost < INF) {
					Integer[] resize = new Integer[p.cost];
					for (int i = 0; i < counts.length; i++) {
						resize[i] = counts[i];
					}
					for (int i = counts.length; i < p.cost; i++) {
						resize[i] = 0;
					}
					resize[p.cost] = 1;
					counts = resize;
				}
				this.reachable++;
			}
		}
		unreachable = this.actors() - reachable;
		return counts;

	}

	/**
	 * prints out the table generated by table();
	 * @param counts - array of counts
	 */
	public void printTable(Integer[]counts) {
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] != 0) {
				System.out.printf("%-10s : %10s", "Number " + i, counts[i]);
				System.out.println();
			}
		}
		if (unreachable != 0) {
			System.out.printf("%11s: %10s", "Unreachable", unreachable);
		}
	}

	/**
	 * Returns a string of all movies the actor 'name' has been in.
	 * @param name - actor to find movies of
	 * @return - string of all movies name has been in
	 */
	public String movies(String name) {
		String e = name + " has been in:" + "\n";
		Vertex v = getVertex(name);
		if (v != null) {
			LinkedList<Edge> edges = v.getEdges();
			Iterator<Edge> itr = edges.iterator();
			while (itr.hasNext()) {
				e  = e + itr.next().to() + "\n";
			}
		} else {
			e = e + "No movies in this database :(";
		}
		return e;
	}


	/**
	 * returns any one of the longest paths in the connected portion of the graph.
	 * @param counts - array generated by table of all counts (used to find longest path length)
	 * @param hm - hash map generated by dijkstra's algorithm.
	 * @return
	 */
	public String longest(Integer[] counts, HashMap<Vertex, Path> hm) {
		int highest = 0;
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] != 0) {
				highest = i;
			}
		}
		Iterator<Vertex> itr = hm.keySet().iterator();
		while (itr.hasNext()) {
			Vertex v = itr.next();
			Path p = hm.get(v);
			if (this.actors.contains(p.dest.data())) {
				if (p.cost == highest) {
					MyGraph.Vertex prev = p.dest;
					String path = prev.data();
					while (prev.prev != null){
						//System.out.println(path);
						path = path + " -> " + prev.prev;
						prev = prev.prev;

					}
					path = path + " (" + p.cost + ")";
					return path;
				}
			}

		}
		return "";
	}

	/**
	 * Prints loading bar for large data reads (initial readIn and topcenter)
	 */
	private void printBar(int numBars, int count, int num){
		String b = "";
		String space = "";
		for (int i = 0; i < 45 - numBars; i++) {
			space = space + " ";
		}
		for (int i = 0; i < numBars; i++) {
			b = b + "|";
		}
		String bar = "{" + b + space + "}";
		System.out.printf("%-45s .... %s", bar, "( " + count + " of " + num + " )" + "\r");

	}
}
