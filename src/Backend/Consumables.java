package Backend;/* Consumables.java
 * Extends from Item.java, covers items to be prepared and consumed by customers
 * Carrie West 10/19/2020
 */

import java.util.ArrayList;

public class Consumables extends Item {
    protected MenuType menu = MenuType.Default;
    protected String expirationDate;

    //build constructor
    public Consumables(long itemNumber,  String MenuType, String itemName, int quantity, String expirationDate){
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.quantity = quantity;
        this.menu = menu.getValue(MenuType);
        this.expirationDate = expirationDate;
    }

    public Consumables(String csv){
        String [] values = csv.split(",");
        this.itemNumber = Long.parseLong(values[0]);
        this.menu = menu.getValue(values[1]);
        this.itemName = values[2];
        this.quantity = Integer.parseInt(values[3]);
        this.expirationDate = (values[4]);
    }

    public Consumables(ArrayList<String> values){
        this.itemNumber = Long.parseLong(values.get(0));
        this.menu = menu.getValue(values.get(1));
        this.itemName = values.get(2);
        this.quantity = Integer.parseInt(values.get(3));
        this.expirationDate = (values.get(4));
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
                break;
            }
            this.itemNumber = occupiedValues.get(occupiedValues.size() - 1) + 1 ;
        }
    }

    public void setQuantity(int value){
        this.quantity = value;
    }


    public long getItemNumber(){
        return this.itemNumber;
    }

    public String getItemName(){
        return this.itemName;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public String getExpirationDate(){
        return this.expirationDate;
    }

    public MenuType getUseCategory() {
        return menu;
    }

    public String toString(){
        String response = this.itemNumber + "," + this.menu.name.toLowerCase() + "," + this.itemName + "," + this.quantity + "," + this.expirationDate;

        return response;
    }
}
