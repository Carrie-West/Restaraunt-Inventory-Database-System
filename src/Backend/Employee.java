package Backend;
/* Employee.java
 * Class representing user actions and interactions with Items
 * Carrie West 10/19/2020
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.soap.Text;
import java.io.*;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Employee {
   private String employeeNumber;
   private String employeeName;
   private String employeePassword;
   private EmployeeType employeeType = EmployeeType.Default;

   //default constructor
   public Employee() {
      this.employeeNumber = "0000";
      this.employeeName = "";
      this.employeePassword = "";
      this.employeeType = EmployeeType.Default;
   }

   //Build Constructor
   public Employee(String employeeNumber, String employeeName, String employeePassword, String typeInput) {
      this.employeeNumber = employeeNumber;
      this.employeeName = employeeName;
      this.employeePassword = employeePassword;
      this.employeeType = employeeType.getValue(typeInput);
   }

   public Employee(ArrayList<String> values){
      this.employeeNumber = values.get(0);
      this.employeeName = values.get(1);
      this.employeePassword = values.get(2);
      this.employeeType = employeeType.getValue(values.get(3));
   }

   public String getEmployeeNumber(){
      return this.employeeNumber;
   }

   //overriding toString()
   public String toString() {
      String response = this.employeeNumber + "," + this.employeeType.name + "," + this.employeeName + "," + this.employeePassword;
      return response;
   }



   public void removeExpiredItems() {
      Connection conn;
      String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");

      try {
         conn = DriverManager.getConnection(url);
         PreparedStatement ps = conn.prepareStatement("DELETE FROM Inventory WHERE expiration_date < ?");

         ps.setString(1, java.time.LocalDate.now().toString());

         ps.executeUpdate();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
   }

   /* displayAllItems(ObservableList<Item> observableInventoryList,
    *    TableView table)
    * Uses the SELECT * sql command to load each set of item data into an
    *    Item object.
    * Each Item object is loaded into an ObservableList.
    * The ObservableList is then used to build a TableView object
    * Returns the TableView to the frontend for display
    */
   public TableView displayAllItems(ObservableList<Item> observableInventoryList, TableView table) {
      table.setEditable(true);
      TableColumn<Item, String> IDColumn = new TableColumn<>("Item ID");
      IDColumn.setCellValueFactory(new PropertyValueFactory("itemNumber"));
      TableColumn<Item, String>  usageType = new TableColumn<>("Usage Type");
      usageType.setCellValueFactory(new PropertyValueFactory("useCategory"));
      TableColumn<Item, String>  itemName = new TableColumn<>("Item Name");
      itemName.setCellValueFactory(new PropertyValueFactory("itemName"));
      TableColumn<Item, Integer>  quantity = new TableColumn<>("Quantity");
      quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
      TableColumn<Item, String>  expirationDate = new TableColumn<>("Expiration Date");
      expirationDate.setCellValueFactory(new PropertyValueFactory("expirationDate"));

      table.getColumns().addAll(IDColumn, usageType, itemName, quantity, expirationDate);
      table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

      if (observableInventoryList.isEmpty()) {
         Connection conn;
         String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
         ResultSet rs;

         try {
            conn = DriverManager.getConnection(url);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Inventory ORDER BY item_id");

            rs = ps.executeQuery();

            while (rs.next()) {
               if (rs.getString("item_id").startsWith("1")) {
                  ArrayList<String> values = new ArrayList<>();
                  values.add(rs.getString(1));
                  values.add(rs.getString(2));
                  values.add(rs.getString(3));
                  values.add(rs.getString(4));
                  Equipment equipment = new Equipment(values);
                  System.out.println(equipment.toString());
                  observableInventoryList.add(equipment);
               } else if (rs.getString("item_id").startsWith("3")) {
                  ArrayList<String> values = new ArrayList<>();
                  values.add(rs.getString(1));
                  values.add(rs.getString(2));
                  values.add(rs.getString(3));
                  values.add(rs.getString(4));
                  values.add(rs.getString(5));
                  Consumables consumable = new Consumables(values);
                  System.out.println(consumable.toString());
                  observableInventoryList.add(consumable);
               }
            }
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }

      table.setItems(observableInventoryList);

      return table;

   }




   /* addItem (ComboBox itemBox, ComboBox usageTypes, TextField nameTextField,
    *    TextField quantityTextField, DatePicker expirationDateCalendar)
    * Takes in JavaFX Objects (ComboBox, TextField, DatePicker)
    * Uses parameter data for build parameters for item
    * Calls createItemNumber() using a SQL call for comparison
    * Updates observableList object with new item, so as to update the
    *    display table
    */
   public void addItem(ComboBox itemBox, ComboBox usageTypes, TextField nameTextField, TextField quantityTextField, DatePicker expirationDateCalendar, ObservableList inventory) throws IOException {
      if (itemBox.getValue() == "Equipment") {
         ArrayList<Long> values = new ArrayList<>();
         try {
            Connection conn;
            String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
            conn = DriverManager.getConnection(url);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Inventory WHERE item_id LIKE '1%' ORDER BY item_id");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               values.add(Long.parseLong(rs.getString(1)));
            }
            Equipment equipment = new Equipment(100, usageTypes.getValue().toString(), nameTextField.getText(), Integer.parseInt(quantityTextField.getText()));

            equipment.createItemNumber(values);

            inventory.add(equipment);
            reportItemChange(equipment.toString(), "ADDED");

            PreparedStatement submission = conn.prepareStatement("INSERT INTO Inventory (item_id, use_id, item_name, quantity) VALUES (?, ?, ?, ?)");
            submission.setLong(1, equipment.getItemNumber());
            submission.setString(2, equipment.getUseCategory().name.toLowerCase());
            submission.setString(3, equipment.getItemName());
            submission.setInt(4, equipment.getQuantity());
            submission.executeUpdate();
         } catch (SQLException throwables) {
            throwables.printStackTrace();
         }

      } else if (itemBox.getValue() == "Consumable") {
         ArrayList<Long> values = new ArrayList<>();
         try {
            Connection conn;
            String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
            conn = DriverManager.getConnection(url);

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Inventory WHERE item_id LIKE '3%' ORDER BY item_id");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               values.add(Long.parseLong(rs.getString(1)));
            }
            Consumables consumable = new Consumables(300, usageTypes.getValue().toString(), nameTextField.getText(), Integer.parseInt(quantityTextField.getText()), expirationDateCalendar.getValue().toString());

            consumable.createItemNumber(values);
            inventory.add(consumable);
            reportItemChange(consumable.toString(), "ADDED");

            PreparedStatement submission = conn.prepareStatement("INSERT INTO Inventory (item_id, use_id, item_name, quantity, expiration_date) VALUES (?, ?, ?, ?, ?)");
            submission.setLong(1, consumable.getItemNumber());
            submission.setString(2, consumable.getUseCategory().name.toLowerCase());
            submission.setString(3, consumable.getItemName());
            submission.setInt(4, consumable.getQuantity());
            submission.setString(5, consumable.getExpirationDate());


            submission.executeUpdate();


         } catch (SQLException throwables) {
            throwables.printStackTrace();
         }
      }


   }


   /* addNewItemToPurchaseOrder (ComboBox itemBox, ComboBox usageTypes, TextField nameTextField, TextField quantityTextField, DatePicker expirationDateCalendar, List<Item> purchaseOrderList)
    * Takes in JavaFX Objects (ComboBox, TextField, DatePicker) and a List<Item> object to be used in an ObservableList
    * Uses parameter data for build parameters for item
    * Calls createItemNumber() using a SQL call for comparison
    * Adds the item to purchaseOrderList
    * Returns tne List object
    */
   public List addNewItemToPurchaseOrder(ComboBox itemBox, ComboBox usageTypes, TextField nameTextField, TextField quantityTextField, DatePicker expirationDateCalendar,List<Item> purchaseOrderList){
      if (itemBox.getValue() == "Equipment"){
         ArrayList<Long> values = new ArrayList<>();
         try {
            Connection conn;
            String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
            conn = DriverManager.getConnection(url);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Inventory WHERE item_id LIKE '1%'");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               values.add(Long.parseLong(rs.getString(1)));
            }
            Equipment equipment = new Equipment(100, usageTypes.getValue().toString(), nameTextField.getText(), Integer.parseInt(quantityTextField.getText()));
            values.forEach(value -> System.out.println(value));
            equipment.createItemNumber(values);

            purchaseOrderList.add(equipment);

         } catch (SQLException throwables) {
            throwables.printStackTrace();
         }

      } else if(itemBox.getValue() == "Consumable"){
         ArrayList<Long> values = new ArrayList<>();
         try {
            Connection conn;
            String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
            conn = DriverManager.getConnection(url);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Inventory WHERE item_id LIKE '3%'");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               values.add(Long.parseLong(rs.getString(1)));
            }
            Consumables consumable = new Consumables(300, usageTypes.getValue().toString(), nameTextField.getText(), Integer.parseInt(quantityTextField.getText()), expirationDateCalendar.getEditor().getText());
            consumable.createItemNumber(values);
            System.out.println(consumable.itemNumber + " " + consumable.itemName);
            purchaseOrderList.add(consumable);


         } catch (SQLException throwables) {
            throwables.printStackTrace();
         }
      }
      return purchaseOrderList;
   }

   /* addExistingItemToPurchaseOrder(TextField itemNumber, TextField quantity, List<Item> purchaseOrderList)
    * Takes in TextField objects for itemNumber and quantity and a List<Item> object to be used in an ObservableList
    * Uses a SQL call to grab item data from the Inventory database and create the Item object
    * Loads Item object into the List object
    */
   public List addExistingItemToPurchaseOrder(TextField itemNumber, TextField quantity, List<Item> purchaseOrderList){
      Connection conn;
      String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
      try {
         conn = DriverManager.getConnection(url);
         PreparedStatement ps = conn.prepareStatement("SELECT * FROM Inventory WHERE item_id = ?");
         ps.setInt(1, Integer.parseInt(itemNumber.getText()));

         ResultSet rs = ps.executeQuery();
         ArrayList<String> values = new ArrayList<>();
         while (rs.next()){
            values.add(rs.getString(1));
            values.add(rs.getString(2));
            values.add(rs.getString(3));
            values.add(rs.getString(4));
            if (rs.getString(5) != null){
               values.add(rs.getString(5));
            }

         }


         if (itemNumber.getText().startsWith("1")){
            Equipment equipment = new Equipment(values);
            equipment.setQuantity(Integer.parseInt(quantity.getText()));
            purchaseOrderList.add(equipment);
         }else if (itemNumber.getText().startsWith("3")){
            Consumables consumable = new Consumables(values);
            consumable.setQuantity(Integer.parseInt(quantity.getText()));
            purchaseOrderList.add(consumable);
         }


      }catch (SQLException e){
         System.out.println(e);
      }
      return purchaseOrderList;
   }




   /* removeItem(TextField itemNumberField)
    * Takes current reading of itemNumberField TextField
    * Searches database for the Item, so it's information can be stored
    *    and reported using reportItemChange()
    * DELETE SQL command for the matching item_id value
    */
   public void removeItem(TextField itemNumberField) throws IOException {
      Connection conn;
      String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
      try{
         conn = DriverManager.getConnection(url);
         PreparedStatement grab = conn.prepareStatement("SELECT * FROM Inventory WHERE item_id = ?");
         grab.setInt(1, Integer.parseInt(itemNumberField.getText()));
         ResultSet rs = grab.executeQuery();
         String values = "";
         while (rs.next()) {
            values += (rs.getString(1)) + " ";
            values += (rs.getString(2)) + " ";
            values += (rs.getString(3)) + " ";
            values += (rs.getString(4)) + " ";
            if (rs.getString(5) != null) {
               values += (rs.getString(5));
            }
            reportItemChange(values, "REMOVED");
         }

         PreparedStatement delete = conn.prepareStatement("DELETE FROM Inventory WHERE item_id = ?");
         delete.setLong(1, Long.parseLong(itemNumberField.getText()));
         delete.executeUpdate();

         }catch (SQLException ex){
         System.out.println(ex);
      }
   }



   /* adjustQuantity(TextField quantityField, TextField itemNumberField)
    * Takes in TextField objects for quantity and itemNumber
    * Feeds quantity and itemNumber data into an UPDATE SQL statement
    * Reloads the item's new data into the item changelog using
    *   reportItemChange()
    */
   public void adjustQuantity(TextField quantityField, TextField itemNumberField) throws IOException {
      Connection conn;
      String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");
      try{
         conn = DriverManager.getConnection(url);
         PreparedStatement ps = conn.prepareStatement("UPDATE Inventory SET quantity = ? WHERE item_id = ?");
         ps.setInt(1, Integer.parseInt(quantityField.getText()));
         ps.setLong(2, Long.parseLong(itemNumberField.getText()));
         ps.executeUpdate();

         PreparedStatement grab = conn.prepareStatement("SELECT * FROM Inventory WHERE item_id = ?");
         grab.setInt(1, Integer.parseInt(itemNumberField.getText()));
         ResultSet rs = grab.executeQuery();
         String values = "";
         while (rs.next()) {
            values += (rs.getString(1)) + " ";
            values += (rs.getString(2)) + " ";
            values += (rs.getString(3)) + " ";
            values += (rs.getString(4)) + " ";
            if (rs.getString(5) != null) {
               values += (rs.getString(5));
            }
            reportItemChange(values, "QUANTITY CHANGED");
         }
      }catch (SQLException ex){
         System.out.println(ex);
      }

   }

   /* createPurchaseOrder(List<Item> purchaseOrder)
    * Takes in List of Item objects
    * Creates a new file with Employee ID, current date and current time
    * Outputs Employee ID, Name and requested items to a new Purchase Order file
    */
   public void createPurchaseOrder(List<Item> purchaseOrder) throws IOException {

      BufferedWriter myWriter = new BufferedWriter(new FileWriter("Purchase Order " + this.employeeNumber + " " + java.time.LocalDate.now()));
      myWriter.write(this.employeeNumber + " " + this.employeeName + " " + java.time.LocalDateTime.now() + "\n");
      for (Item item : purchaseOrder) {
         try {
            myWriter.write(item.toString() + "\n");
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      myWriter.close();
   }

   /* reportItemChange(String itemChanged, String change)
    * Takes in name of item being changed and type of change
    * Creates a new file with Items Changed and the Current Date
    *    if it does not currently exist
    * Buffered writes to the file with the time of the change, the Employee's
    *    ID and name, as well as the itemChanged and type of change.
    */
   public void reportItemChange(String itemChanged, String change) throws IOException{

      BufferedWriter myWriter;


      File changeLog = new File("Items Changed " + java.time.LocalDate.now());
      myWriter = new BufferedWriter(new FileWriter((changeLog) , true));
      myWriter.append("\n");
      myWriter.append(java.time.LocalTime.now() + " " + this.employeeNumber + " " + this.employeeName + "\n");

      try {
         myWriter.write(change + " " + itemChanged + "\n");
      } catch (IOException e) {
         e.printStackTrace();
      }

      myWriter.close();
   }


}

