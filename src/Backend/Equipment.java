package Backend;/* Equipment.java
 * Extending from Item.java, covers the equipment used in a restaraunt setting
 * Examples: Silverware, Appliances, Uniform pieces
 * Carrie West 10/19/2020
 */

import java.util.ArrayList;

public class Equipment extends Item {
    protected KitchenUse useCategory = KitchenUse.Sanitation;

    //build constructor
    public Equipment(long itemNumber, String kitchenUse, String itemName, int quantity){
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.quantity = quantity;
        this.useCategory = useCategory.getValue(kitchenUse);
    }

    //csv constructor
    public Equipment(String csv){
        String [] values = csv.split(",");
        this.itemNumber = Long.parseLong(values[0]);
        this.useCategory = useCategory.getValue(values[1]);
        this.itemName = values[2];
        this.quantity = Integer.parseInt(values[3]);
    }

    public Equipment(ArrayList<String> values){
        this.itemNumber = Long.parseLong(values.get(0));
        this.useCategory = useCategory.getValue(values.get(1));
        this.itemName = values.get(2);
        this.quantity = Integer.parseInt(values.get(3));
    }

    public void setQuantity(int value){
        this.quantity = value;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public long getItemNumber(){
        return this.itemNumber;
    }

    public String getItemName(){
        return this.itemName;
    }

    public KitchenUse getUseCategory() {
        return useCategory;
    }

    /* createItemNumber(ArrayList<Long>occupiedValues)
     * Iterates over occupiedValues, looking for either the end of the list
     * or a break in the order to use as the new value
     * Sets itemNumber to the first available value
     */
    public void createItemNumber(ArrayList<Long> occupiedValues){
        for (long i = occupiedValues.get(0); i < occupiedValues.get(occupiedValues.size() -1 ); i++){
            if (!occupiedValues.contains(i)){
                this.itemNumber = i;
            }else{
                this.itemNumber = occupiedValues.get(occupiedValues.size() - 1) + 1 ;
            }
        }
    }

    public String toString(){
        String response = this.itemNumber + "," + this.useCategory.name.toLowerCase() + "," + this.itemName + "," + this.quantity;

        return response;
    }

}
