package Backend;/* Item.java
 * Abstract class from which Equipment.java and Consumables.java extend
 * Carrie West 10/19/2020
 */
import java.util.ArrayList;

public abstract class Item {
    protected long itemNumber;
    protected String itemName;
    protected int quantity;

   abstract protected void createItemNumber(ArrayList<Long> occupiedValues);

   abstract public long getItemNumber();

   abstract public void setQuantity(int value);

   abstract public int getQuantity();

   abstract public String getItemName();

   @Override
   abstract public String toString();
}
