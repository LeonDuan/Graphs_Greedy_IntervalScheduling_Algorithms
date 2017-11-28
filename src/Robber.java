import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Robber {

    /*
     * This method should return true if the robber can rob all the houses in the neighborhood,
     * which are represented as a graph, and false if he cannot. The function should also print to the console the
     * order in which the robber should rob the houses if he can rob the houses. You do not need to print anything
     * if all the houses cannot be robbed.
     */

    //things I need to keep track of when running DFS
    private Set<String> housesToRob = new HashSet<>();
    private Map<String, Integer> keyGathered = new HashMap<>();
    private Map<String, Integer> keyNeeded = new HashMap<>();
    private Set<String> result = new LinkedHashSet<>();
    private List<Item> ingredRobbed = new ArrayList<>();

    public void DFS(String house, Graph neighborhood){
        if(result.contains(house)) return;
        //update keyGathered
        int origKeyNum = keyGathered.getOrDefault(house,0);
        keyGathered.put(house, origKeyNum+1);

        //check if I have enough keys so I can proceed
        if (keyGathered.get(house) >= keyNeeded.get(house)){
            result.add(house);
            housesToRob.remove(house);
            HashSet<String> neighbors = neighborhood.graph.get(house);
            if(neighbors == null) return;
            for(String neighborHouse: neighbors){
/*            for(String neighborHouse: neighborhood.graph.get(house)){*/
                DFS(neighborHouse, neighborhood);
            }
        }
        return;
    }

    public boolean canRobAllHouses(Graph neighborhood) {
        //create a set that tells me how many keys needed for each house
        Iterator it = neighborhood.graph.keySet().iterator();
        while(it.hasNext()){
            String house = it.next().toString();
            keyGathered.put(house,0);
            housesToRob.add(house);
            Set<String> key_house_holds = neighborhood.graph.get(house);

            //visit all houses the the current house has keys to
            for(String key : key_house_holds){
                if(keyNeeded.getOrDefault(key,0) == 0){
                    keyNeeded.put(key,1);
                }
                else{
                    keyNeeded.put(key,keyNeeded.get(key) + 1);
                }
            }
            for(String unlockedHouse: neighborhood.unlocked){
                keyNeeded.put(unlockedHouse,0);
            }
        }

        //start from every unlocked house
        for(String unlocked_house : neighborhood.unlocked){
            DFS(unlocked_house, neighborhood);
        }

        if (result.size()==neighborhood.graph.size()) {
            Iterator i = result.iterator();
            while (i.hasNext()) {
                String house = i.next().toString();
                System.out.print(house);
                if (i.hasNext()) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
        else{
            System.out.println("No solution");
        }
        return housesToRob.size() == 0;
    }

    /*
     *
     */
    public void maximizeLoot(String lootList) {
        try {
            List<Item> itemList = new ArrayList<Item>();
            BufferedReader br = new BufferedReader(new FileReader(lootList));
            String item_info_string = br.readLine();
            double capacity_left = Double.parseDouble(item_info_string);

            item_info_string = br.readLine();
            while(item_info_string != null) {
                //use line from ingredient.txt to construct a new Item object
                String[] info = item_info_string.split(",");
                String itemName = info[0];
                double quantity = Double.parseDouble(info[1]);
                double value = Double.parseDouble(info[2]);
                Item item = new Item(itemName, quantity, value);
                itemList.add(item);
                item_info_string = br.readLine();

            }

            //sort by highest value per pound
            itemList.sort(new ItemComparator());
            Collections.reverse(itemList);

            //greedy algorithm to rob things
            Iterator<Item> it = itemList.iterator();
            while(it.hasNext()){
                Item currentItem = it.next();
                if(capacity_left <= currentItem.quantity){
                    currentItem.quantity = capacity_left;
                    ingredRobbed.add(currentItem);
                    break;
                }
                else{
                    ingredRobbed.add(currentItem);
                    capacity_left -= currentItem.quantity;
                }
            }
            it = ingredRobbed.iterator();
            while(it.hasNext()){
                Item robbedItem = it.next();
                String itemResult = "";
                itemResult += robbedItem.name + " ";
                itemResult += Double.toString(Math.ceil(robbedItem.quantity * 100)/100);
                System.out.println(itemResult);
            }
            br.close();


        }
        catch (IOException e){
            System.err.println("No Such File.");
        }

    }

    public void scheduleMeetings(String buyerList) {
        List<Buyer> buyers = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(buyerList));
            String buyerInfoString = br.readLine();
            while(buyerInfoString != null){

                //extract time info
                String[] buyerInfo = buyerInfoString.split(", ");
                String buyer_name = buyerInfo[0];
                String[] duration = buyerInfo[1].split("-");
                String startTimeString = duration[0];
                String endTimeString = duration[1];
                int start = timeStrToInt(startTimeString);
                int end = timeStrToInt(endTimeString) + 15;

                //add new buyer
                buyers.add(new Buyer(buyer_name, start, end));
                buyerInfoString = br.readLine();
            }
        }
        catch (IOException e){
            System.err.println("No Such File");
        }
        buyers.sort(new EndTimeComparator());


        //greedy algorithm by earliest finish time
        int currentLatest = 0;
        List<Buyer> willMeet = new ArrayList<>();
        for(Buyer buyer: buyers){
            if(buyer.start >= currentLatest){
                willMeet.add(buyer);
                currentLatest = buyer.end;
            }
        }

        //display who FruitCake will meet
        for(Buyer buyer: willMeet){
            System.out.println(buyer.name);
        }


    }

    //convert a time string to time integer
    private int timeStrToInt(String timeString) {
        int time = 0;

        //separate time and am/pm
        if(timeString.contains("pm")){
            time += 60 * 12;
        }
        timeString = timeString.replace("pm","");
        timeString = timeString.replace("am","");
        //separate hour and minute
        String[] timeArr = timeString.split(":");
        if(timeArr.length == 1){
            time += Integer.parseInt(timeArr[0]) * 60;
        }
        else if(timeArr.length == 2){
            time += Integer.parseInt(timeArr[0]) * 60 + Integer.parseInt(timeArr[1]);
        }
        //reserve 15 minutes
        return time;
    }

    //comparator to compare time
    class EndTimeComparator implements Comparator{
        public int compare(Object obj1, Object obj2){
            Buyer buyer1 = (Buyer) obj1;
            Buyer buyer2 = (Buyer) obj2;
            if(buyer1.end > buyer2.end){
                return 1;
            }
            else if(buyer1.end == buyer2.end){
                return 0;
            }
            else{
                return -1;
            }
        }
    }

    //comparator to compare value per pound between items
    class ItemComparator implements Comparator{
        public int compare(Object obj1, Object obj2){
            Item item1 = (Item) obj1;
            Item item2 = (Item) obj2;
            if(item1.value > item2.value){
                return 1;
            }
            else if(item1.value == item2.value){
                return 0;
            }
            else{
                return -1;
            }
        }
    }

}
