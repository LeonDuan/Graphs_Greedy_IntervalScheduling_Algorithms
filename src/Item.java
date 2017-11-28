/**
 * Created by Leon on 03/06/2017.
 */
public class Item {
    public String name;
    public double quantity;
    public double value;

    public Item(String name, double quantity, double value){
        this.name = name;
        this.quantity = quantity;
        this.value = value;
    }

    public int compareTo(Item other){
        if((this.value - other.value) > 0 ){
            return 1;
        }
        else if((this.value - other.value) == 0){
            return 0;
        }
        else{
            return -1;
        }
    }
    public double compare(Item item1, Item item2){
        return item1.compareTo(item2);
    }
}
