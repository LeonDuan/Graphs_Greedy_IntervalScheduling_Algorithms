import sun.security.provider.certpath.AdjacencyList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Graph {

    /*
     * Creates a graph to represent the neighborhood, where unlocked is the file name for the unlocked houses
     * and keys is the file name for which houses have which keys.
     */
    public Set<String> unlocked = new HashSet<>();
    public Map<String, HashSet<String>> graph = new HashMap<>();

    public Graph(String house, String keys) {
        //process unlocked houses
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(house));
            BufferedReader br2 = new BufferedReader(new FileReader(keys));
            String temp_house = br1.readLine();
            while(temp_house != null){
                unlocked.add(temp_house);
                temp_house = br1.readLine();
            }

            String keyInHouse = br2.readLine();
            while(keyInHouse != null){
                boolean HouseAdded = false;
                String currentHouse = "";
                String [] keyInHouseList = keyInHouse.split(": |, ");

                for(String houseToExamine: keyInHouseList){
                    if(!HouseAdded){
                        graph.put(houseToExamine,new HashSet<>());
                        currentHouse = houseToExamine;
                        HouseAdded = true;
                    }
                    else{
                        HashSet<String> cur_house = graph.get(currentHouse);
                        cur_house.add(houseToExamine);
                    }
                }
               keyInHouse =  br2.readLine();
            }
            br1.close();
            br2.close();
        }
        catch(FileNotFoundException e){
            System.out.println("One or More Files Not Found.");
            System.exit(1);
        }
        catch (IOException e){
            System.out.println("IOException");
            System.exit(1);
        }

    }

    /*
     * This method should return true if the Graph contains the vertex described by the input String.
     */
    public boolean containsVertex(String node) {
        return graph.containsKey(node);
    }

    /*
     * This method should return true if there is a direct edge from the vertex
     * represented by start String and end String.
     */
    public boolean containsEdge(String start, String end) {
        if(graph.containsKey(start)){
            Set house = graph.get(start);
            return house.contains(end);
        }
        return false;
    }

    /*
     * This method returns true if the house represented by the input String is locked
     * and false is the house has been left unlocked.
     */
    public boolean isLocked(String house) {
        return !unlocked.contains(house);
    }
}
