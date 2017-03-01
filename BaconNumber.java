import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Reads in a database, then allows the user to find data pertaining to the Bacon Number of that database.
 * Allows the user to find the Bacon Number and path from the current center to a destination actor.
 * Allows the user to recenter the current graph to a new actor.
 * Allows the user to find the average bacon number from the current center to all other actors.
 * Allows the user to find the centers with the lowest average bacon numbers in the graph.
 * Allows the user to create a table of the counts of bacon numbers for the given center from 0 up to the longest.
 * Allows the user to find all movies an actor was in.
 * Allows the user to find one of the longest paths in the database from the center.
 * @author Emily Rizzo
 *
 */

public class BaconNumber {

    /**
     * @param args
     */
    public static void main(String[] args) {
	MyGraph graph = new MyGraph();
	String source = args[0];
	
	int lines = 0;
	if (source.contains("small")){
		lines = 1817;
	} else if (source.contains("top250")){
		lines=14339;
	} else if (source.contains("pre1950")){
		lines=1014465;
	} else if (source.contains("post1950")){
		lines=8159857;
	} else if (source.contains("only-tv-v")){
		lines = 2302907;
	} else if (source.contains("no-tv-v")){
		lines = 6871415;
	} else if (source.contains("full")){
		lines = 9174322;
	}
	String center = "Kevin Bacon (I)";
	if (args.length > 1){
	    center = args[1];
	}
	graph.readIn(source, lines);
	System.out.println();
	System.out.println("Creating Hash Map using Dijkstra's Algorithm...");
	//System.out.println(graph);
	Scanner input = new Scanner(System.in);
	MyGraph.Vertex c = graph.getVertex(center);
	while (c == null) {
	    System.out.println("Vertex \"" + center + "\" is not in the graph. Please enter a name from the following: ");
	    Iterator itr = graph.actors.iterator();
	    int max = 0;
	    while (itr.hasNext() && max < 20) {
	    	MyGraph.Vertex check = graph.getVertex((String) itr.next());
	    	if (check.getEdges().size() > 10){
	    		System.out.println(check + " || " + check.getEdges().size());
	    		max++;
	    	}
	    }
	    System.out.print("Enter a new center: ");
	    if (input.hasNextLine()) {
	    	center = input.nextLine();
	    	c = graph.getVertex(center);
	    }

	}
	System.out.println (c + ", " + c.getEdges().size());

	HashMap<MyGraph.Vertex, MyGraph.Path> hm = graph.dijkstra(c);

	String line;
	Scanner scan;
//	System.out.println("HashMap: " + hm.toString());

	System.out.println(); System.out.println();
	System.out.println("Welcome to Kevin Bacon! Kevin Bacon welcomes you.");
	System.out.println();
	System.out.println("Enter \"help\" for a list of commands or \"exit\" to exit the system.");
	System.out.print("Please enter command: ");
	while (input.hasNextLine()){
	    System.out.println();
	    line = input.nextLine();
	    scan = new Scanner(line);
	    String command = scan.next();
	    command = command.toLowerCase();

	    if(command.equals("exit")){
		System.exit(0);
	    } else

		if (command.equals("find")){			//find the path from the center 
		    String name = "";
		    while (scan.hasNext()){
			name = name + scan.next() + " ";
		    }
		    name = name.substring(0, name.length()-1);
		    System.out.println(graph.find(hm, name));
		    System.out.println();
		} else

		if (command.equals("recenter")) {
			String name = "";
			while (scan.hasNext()){
			    name = name + scan.next() + " ";
			}
			name = name.substring(0, name.length()-1);
			c = graph.getVertex(name);
			if (c != null) {
			    System.out.println("Recentering to \"" + name + "\" ...");
			    center = name;
			    hm = graph.dijkstra(c);
			} else {
			    System.out.println("Vertex \"" + name + "\" does not exist in the graph.");
			} 
			System.out.println();
		} else if (command.equals("avgdist")) {
			graph.loadConnected(hm);
			float avg = graph.avgDist(hm);
			System.out.println(avg + "\t" + center + "\t( " + graph.reachable() + ", " + graph.unreachable + " )");
			System.out.println();
		} else if (command.equals("topcenter")) {
			graph.loadConnected(hm);
			int n = 5;
			if (scan.hasNext()) {
			    n = Integer.parseInt(scan.next());
			}
			graph.topcenter(n, hm);
			System.out.println();
		} else if (command.equals("table")) {
			Integer[] counts = graph.table(hm);
			graph.printTable(counts);
			System.out.println();
		    } else if (command.equals("movies")){
			String name = "";
			while (scan.hasNext()){
			    name = name + scan.next() + " ";
			}
			name = name.substring(0, name.length()-1);
			
			System.out.println(graph.movies(name));
			
		} else if(command.equals("longest")){
			Integer[] counts = graph.table(hm);
			System.out.println(graph.longest(counts, hm));
		}else if(command.equals("help")) {
			System.out.printf("%-15s : %s", "(Command)", " (Function)");
			System.out.println();
			System.out.printf("%-15s : %s", "find <name>", " finds the shortest path from center to name.");
			System.out.println();
			System.out.printf("%-15s : %s", "recenter <name>", " recenters to the given name.");
			System.out.println();
			System.out.printf("%-15s : %s", "avgdist", " finds the average bacon number w/respect to the center.");
			System.out.println();
			System.out.printf("%-15s : %s", "topcenter <n>", " finds the top n centers for the graph,"); System.out.println();
			System.out.printf("%18s %s", "", "i.e. the n actors with the shortest average bacon number.");
			System.out.println();
			System.out.printf("%-15s : %s", "table", " prints a table of the counts of bacon numbers"); System.out.println();
			System.out.printf("%18s %s", "", "for the given center from 0 up to the longest.");
			System.out.println();
			System.out.printf("%-15s : %s", "movies <name>", " prints a list of all numbers <name> was in");
			System.out.println();
			System.out.printf("%-15s : %s", "longest", " prints one path of longest possible length in the graph");
			System.out.println();
			System.out.println();
		    } else {
			System.out.println("Not a valid command. Enter \"help\" for a list of valid commands.");
		    }

	    System.out.println("Enter \"help\" for a list of commands or \"exit\" to exit the system.");
	    System.out.print("Please enter command: ");
	}
    }
    
 

}
