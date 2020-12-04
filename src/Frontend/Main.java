package Frontend;

import Backend.Consumables;
import Backend.Employee;
import Backend.Equipment;
import Backend.Item;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Welcome");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String checkUser = userTextField.getText();
                String checkPass = pwBox.getText();
                Connection conn;
                String url = ("jdbc:sqlite:C:/Program Files/SQLiteStudio/RestaurantInventory");

                try {
                    conn = DriverManager.getConnection(url);
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM EmployeeData WHERE employee_id = '" + checkUser + "'");

                    ResultSet rs = ps.executeQuery();
                    ArrayList<String> values = new ArrayList<>();
                    while (rs.next()) {
                        values.add(rs.getString(1));
                        values.add(rs.getString(2));
                        values.add(rs.getString(3));
                        values.add(rs.getString(4));
                    }

                    if (checkPass.equals(values.get(2))) {
                        actionTarget.setFill(Color.GREEN);
                        actionTarget.setText("Login Successful.");
                        Employee user = new Employee(values);
                        primaryStage.close();
                        program(user);

                    } else {
                        actionTarget.setFill(Color.RED);
                        actionTarget.setText("Incorrect Password.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }


            }

        });


        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);


        primaryStage.show();
    }

    public void program(Employee user) {

        user.removeExpiredItems();

        Stage secondaryStage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        secondaryStage.setTitle("Restaurant Inventory Manager");

        ColumnConstraints tableColumn = new ColumnConstraints();
        tableColumn.setPercentWidth(90);
        grid.getColumnConstraints().add(tableColumn);
        RowConstraints tableRow = new RowConstraints();
        tableRow.setPercentHeight(100);
        grid.getRowConstraints().add(tableRow);
        for (int i = 0; i < 2; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(20);
            grid.getColumnConstraints().add(column);
        }

        List<Item> inventoryList = new ArrayList<>();
        ObservableList<Item> observableInventoryList = FXCollections.observableList(inventoryList);
        TableView runningTable = new TableView();
        user.displayAllItems(observableInventoryList, runningTable);

        grid.add(runningTable, 0, 0);

        Button purchase_order = new Button("Create Purchase Order");
        Button add_item = new Button("Add Item");
        Button remove_item = new Button("Remove Item");
        Button adjust_quantity = new Button("Adjust Item Quantity");


        VBox vbBtn = new VBox(10);
        vbBtn.setAlignment(Pos.CENTER_LEFT);
        vbBtn.getChildren().add(purchase_order);
        vbBtn.getChildren().add(add_item);
        vbBtn.getChildren().add(remove_item);
        vbBtn.getChildren().add(adjust_quantity);
        grid.add(vbBtn, 2, 0);

        purchase_order.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Stage tertiaryStage = new Stage();
                GridPane purchaseOrderGrid = new GridPane();
                purchaseOrderGrid.setAlignment(Pos.BASELINE_LEFT);
                purchaseOrderGrid.setPrefSize(800, 400); // Default width and height
                purchaseOrderGrid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
                purchaseOrderGrid.setHgap(10);
                purchaseOrderGrid.setVgap(10);
                purchaseOrderGrid.setPadding(new Insets(25, 25, 25, 25));
                ColumnConstraints tableColumn = new ColumnConstraints();
                tableColumn.setPercentWidth(80);
                purchaseOrderGrid.getColumnConstraints().add(tableColumn);
                RowConstraints tableRow = new RowConstraints();
                tableRow.setPercentHeight(50);
                purchaseOrderGrid.getRowConstraints().add(tableRow);
                for (int i = 0; i < 2; i++) {
                    ColumnConstraints column = new ColumnConstraints();
                    column.setPercentWidth(10);
                    purchaseOrderGrid.getColumnConstraints().add(column);
                }
                for (int i = 0; i < 2; i++) {
                    RowConstraints row = new RowConstraints();
                    row.setPercentHeight(25);
                    purchaseOrderGrid.getRowConstraints().add(row);
                }


                tertiaryStage.setTitle("Create Purchase Order");


                TableView<Item> purchaseOrderTable = new TableView();
                purchaseOrderTable.setEditable(true);
                TableColumn<Item, String> IDColumn = new TableColumn<>("Item ID");
                IDColumn.setCellValueFactory(new PropertyValueFactory("itemNumber"));
                TableColumn<Item, String> usageType = new TableColumn<>("Usage Type");
                usageType.setCellValueFactory(new PropertyValueFactory("useCategory"));
                TableColumn<Item, String> itemName = new TableColumn<>("Item Name");
                itemName.setCellValueFactory(new PropertyValueFactory("itemName"));
                TableColumn<Item, Integer> quantity = new TableColumn<>("Quantity");
                quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                TableColumn<Item, String> expirationDate = new TableColumn<>("Expiration Date");
                expirationDate.setCellValueFactory(new PropertyValueFactory("expirationDate"));


                List<Item> purchaseOrderList = new ArrayList<>();
                ObservableList<Item> observablePurchaseOrderList = FXCollections.observableList(purchaseOrderList);
                purchaseOrderTable.getColumns().addAll(IDColumn, usageType, itemName, quantity, expirationDate);
                purchaseOrderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                purchaseOrderTable.setItems(observablePurchaseOrderList);
                purchaseOrderTable.setEditable(true);


                purchaseOrderGrid.add(purchaseOrderTable, 0, 1);

                TableView runningComparisonTable = new TableView();
                user.displayAllItems(observableInventoryList, runningComparisonTable);


                purchaseOrderGrid.add(runningComparisonTable, 0, 0);

                Button new_item = new Button("Enter New Item");

                new_item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        Stage quarternaryStage = new Stage();
                        GridPane grid = new GridPane();
                        grid.setAlignment(Pos.CENTER);
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(25, 25, 25, 25));
                        tertiaryStage.setTitle("Add Item");


                        ColumnConstraints column = new ColumnConstraints();
                        column.setPercentWidth(100);
                        grid.getColumnConstraints().add(column);

                        for (int i = 0; i < 5; i++) {
                            RowConstraints row = new RowConstraints();
                            row.setPercentHeight(20);
                            grid.getRowConstraints().add(row);
                        }

                        ObservableList<String> itemType =
                                FXCollections.observableArrayList(
                                        "Equipment",
                                        "Consumable"
                                );

                        ComboBox itemBox = new ComboBox(itemType);


                        ObservableList<String> equipmentType =
                                FXCollections.observableArrayList(
                                        "Storage",
                                        "Cooking",
                                        "Preparation",
                                        "Serving",
                                        "Sanitation",
                                        "Uniform"
                                );

                        ObservableList<String> consumablesType =
                                FXCollections.observableArrayList(
                                        "Breakfast",
                                        "Lunch",
                                        "Dinner",
                                        "Dessert",
                                        "Alcohol"
                                );
                        ComboBox<String> usageTypes = new ComboBox<>();

                        itemBox.valueProperty().addListener((obs, oldValue, newValue) -> {
                            if (newValue == null) {
                                usageTypes.getItems().clear();
                                usageTypes.setDisable(true);
                            } else if (newValue == "Equipment") {
                                usageTypes.getItems().setAll(equipmentType);
                            } else if (newValue == "Consumable") {
                                usageTypes.getItems().setAll(consumablesType);

                            }
                        });


                        grid.add(itemBox, 0, 0);

                        TextField nameTextField = new TextField("Name");
                        grid.add(nameTextField, 0, 1);

                        TextField quantityTextField = new TextField("Quantity");
                        grid.add(quantityTextField, 0, 2);

                        grid.add(usageTypes, 0, 3);

                        DatePicker expirationDateCalendar = new DatePicker();
                        grid.add(expirationDateCalendar, 0, 4);
                        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        expirationDateCalendar.setConverter(new StringConverter<LocalDate>() {

                            @Override
                            public String toString(LocalDate date) {
                                if (date != null) {
                                    return dateFormatter.format(date);
                                } else {
                                    return "";
                                }
                            }

                            @Override
                            public LocalDate fromString(String string) {
                                if (string != null && !string.isEmpty()) {
                                    return LocalDate.parse(string, dateFormatter);
                                } else {
                                    return null;
                                }

                            }
                        });

                        Button submit_button = new Button("Submit");

                        submit_button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                user.addNewItemToPurchaseOrder(itemBox, usageTypes, nameTextField, quantityTextField, expirationDateCalendar, purchaseOrderList);

                                runningTable.refresh();
                                purchaseOrderTable.refresh();


                            }
                        });

                        Button done_button = new Button("Done");

                        done_button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                quarternaryStage.close();
                            }
                        });

                        grid.add(submit_button, 0, 5);
                        grid.add(done_button, 1, 5);


                        Scene scene = new Scene(grid, 300, 275);
                        quarternaryStage.setScene(scene);

                        quarternaryStage.show();
                    }
                });

                Button existing_item = new Button("Select Existing Item");

                existing_item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Stage quarternaryStage = new Stage();
                        GridPane grid = new GridPane();
                        grid.setAlignment(Pos.CENTER);
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(25, 25, 25, 25));
                        quarternaryStage.setTitle("Add an Existing Item");


                        ColumnConstraints column = new ColumnConstraints();
                        column.setPercentWidth(100);
                        grid.getColumnConstraints().add(column);

                        for (int i = 0; i < 3; i++) {
                            RowConstraints row = new RowConstraints();
                            row.setPercentHeight(33);
                            grid.getRowConstraints().add(row);
                        }

                        TextField itemNumber = new TextField("Item Number");

                        grid.add(itemNumber, 0, 1);

                        TextField quantity = new TextField("Quantity");
                        grid.add(quantity, 0, 2);

                        Button submit_button = new Button("Submit");
                        submit_button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                user.addExistingItemToPurchaseOrder(itemNumber, quantity, purchaseOrderList);
                                purchaseOrderTable.refresh();


                            }
                        });
                        grid.add(submit_button, 0, 3);

                        Button done_button = new Button("Done");
                        done_button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                quarternaryStage.close();
                            }
                        });
                        grid.add(done_button, 1, 3);

                        Scene scene = new Scene(grid, 300, 275);

                        quarternaryStage.setScene(scene);

                        quarternaryStage.show();


                    }
                });

                VBox itemOptions = new VBox(10);
                itemOptions.setAlignment(Pos.CENTER_RIGHT);
                itemOptions.getChildren().add(new_item);
                itemOptions.getChildren().add(existing_item);
                purchaseOrderGrid.add(itemOptions, 2, 0);

                Button done_button = new Button("Done");

                done_button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            user.createPurchaseOrder(purchaseOrderList);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        tertiaryStage.close();
                    }
                });
                VBox menuOptions = new VBox(10);
                menuOptions.setAlignment(Pos.CENTER_RIGHT);
                menuOptions.getChildren().add(done_button);
                purchaseOrderGrid.add(menuOptions, 2, 2);


                Scene scene = new Scene(purchaseOrderGrid, 1200, 600);
                tertiaryStage.setScene(scene);

                tertiaryStage.show();
            }
        });
        add_item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Stage tertiaryStage = new Stage();
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(25, 25, 25, 25));
                tertiaryStage.setTitle("Add Item");


                ColumnConstraints column = new ColumnConstraints();
                column.setPercentWidth(100);
                grid.getColumnConstraints().add(column);

                for (int i = 0; i < 5; i++) {
                    RowConstraints row = new RowConstraints();
                    row.setPercentHeight(20);
                    grid.getRowConstraints().add(row);
                }

                ObservableList<String> itemType =
                        FXCollections.observableArrayList(
                                "Equipment",
                                "Consumable"
                        );

                ComboBox itemBox = new ComboBox(itemType);


                ObservableList<String> equipmentType =
                        FXCollections.observableArrayList(
                                "Storage",
                                "Cooking",
                                "Preparation",
                                "Serving",
                                "Sanitation",
                                "Uniform"
                        );

                ObservableList<String> consumablesType =
                        FXCollections.observableArrayList(
                                "Breakfast",
                                "Lunch",
                                "Dinner",
                                "Dessert",
                                "Alcohol"
                        );
                ComboBox<String> usageTypes = new ComboBox<>();

                itemBox.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue == null) {
                        usageTypes.getItems().clear();
                        usageTypes.setDisable(true);
                    } else if (newValue == "Equipment") {
                        usageTypes.getItems().setAll(equipmentType);
                    } else if (newValue == "Consumable") {
                        usageTypes.getItems().setAll(consumablesType);

                    }
                });


                grid.add(itemBox, 0, 0);

                TextField nameTextField = new TextField("Name");
                grid.add(nameTextField, 0, 1);

                TextField quantityTextField = new TextField("Quantity");
                grid.add(quantityTextField, 0, 2);

                grid.add(usageTypes, 0, 3);

                DatePicker expirationDateCalendar = new DatePicker();
                expirationDateCalendar.setConverter(new StringConverter<LocalDate>() {
                    final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    @Override
                    public String toString(LocalDate date) {
                        if (date != null) {
                            return dateFormatter.format(date);
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public LocalDate fromString(String string) {
                        if (string != null && !string.isEmpty()) {
                            return LocalDate.parse(string, dateFormatter);
                        } else {
                            return null;
                        }

                    }
                });
                grid.add(expirationDateCalendar, 0, 4);

                Button submit_button = new Button("Submit");


                submit_button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        try {
                            user.addItem(itemBox, usageTypes, nameTextField, quantityTextField, expirationDateCalendar, observableInventoryList);

                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }



                    }


                });

                Button done_button = new Button("Done");

                done_button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tertiaryStage.close();
                    }
                });

                grid.add(submit_button, 0, 5);
                grid.add(done_button, 1, 5);


                Scene scene = new Scene(grid, 300, 275);
                tertiaryStage.setScene(scene);

                tertiaryStage.show();
            }
        });
        remove_item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Stage tertiaryStage = new Stage();
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(25, 25, 25, 25));
                tertiaryStage.setTitle("Remove Item");

                ColumnConstraints column = new ColumnConstraints();
                column.setPercentWidth(100);
                grid.getColumnConstraints().add(column);

                for (int i = 0; i < 2; i++) {
                    RowConstraints row = new RowConstraints();
                    row.setPercentHeight(50);
                    grid.getRowConstraints().add(row);
                }


                TextField itemNumberField = new TextField("Item Number");

                grid.add(itemNumberField, 0, 0);

                Button submit_button = new Button("Submit");

                submit_button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            user.removeItem(itemNumberField);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        runningTable.refresh();
                    }
                });

                grid.add(submit_button, 0, 1);


                Button done_button = new Button("Done");
                done_button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tertiaryStage.close();
                    }
                });
                grid.add(done_button, 1, 1);

                Scene scene = new Scene(grid, 300, 275);
                tertiaryStage.setScene(scene);

                tertiaryStage.show();
            }
        });
        adjust_quantity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Stage tertiaryStage = new Stage();
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(25, 25, 25, 25));
                tertiaryStage.setTitle("Adjust Item Quantity");

                ColumnConstraints column = new ColumnConstraints();
                column.setPercentWidth(100);
                grid.getColumnConstraints().add(column);

                for (int i = 0; i < 3; i++) {
                    RowConstraints row = new RowConstraints();
                    row.setPercentHeight(33);
                    grid.getRowConstraints().add(row);
                }


                TextField itemNumberField = new TextField("Item Number");

                grid.add(itemNumberField, 0, 0);

                TextField quantityField = new TextField("Quantity");

                grid.add(quantityField, 0, 1);

                Button submit_button = new Button("Submit");

                submit_button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            user.adjustQuantity(quantityField, itemNumberField);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });

                grid.add(submit_button, 0, 2);

                Button done_button = new Button("Done");

                done_button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tertiaryStage.close();
                    }
                });

                grid.add(done_button, 1, 2);

                Scene scene = new Scene(grid, 300, 275);
                tertiaryStage.setScene(scene);

                tertiaryStage.show();
            }
        });


        Scene scene = new Scene(grid, 1200, 600);
        secondaryStage.setScene(scene);
        secondaryStage.show();
    }

    public static void main(String[] args) {


        launch(args);
    }
}


