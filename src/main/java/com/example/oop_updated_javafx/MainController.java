package com.example.oop_updated_javafx;

import com.example.oop_updated_javafx.model.DatabaseConnection;
import com.example.oop_updated_javafx.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

public class MainController {

    @FXML
    private TableView<User> TableContainer;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;

    public void initialize() {
        buildTable();
        editCell();
    }
    public void addUser() throws IOException {

        if (!nameField.getText().isBlank() && !emailField.getText().isBlank()) {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            String insertValues = "INSERT INTO Users(name, email) VALUES ('"+ nameField.getText() + "','" + emailField.getText() + "')";

            try {
                connectDB.createStatement().executeUpdate(insertValues);
            } catch (Exception e) {
                e.printStackTrace();
            }

            nameField.clear();
            emailField.clear();

            buildTable();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
        }
    }

    private ObservableList<User> data;
    public void buildTable() {
       DatabaseConnection connectNow = new DatabaseConnection();
       Connection connectDB = connectNow.getConnection();

       String UserViewQuery = "SELECT name, email FROM Users";

       data = FXCollections.observableArrayList();

       try {
           ResultSet resultSet = connectDB.createStatement().executeQuery(UserViewQuery);
              while (resultSet.next()) {
                data.add(new User(
                          resultSet.getString("name"),
                          resultSet.getString("email")
                ));
              }
              nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
              emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
              TableContainer.setItems(data);

       } catch (Exception e) {
              e.printStackTrace();
              System.out.println("Error on Building Data");
       }
    }

    public void editCell() {
        TableContainer.setEditable(true);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String newName = event.getNewValue();
            user.setName(newName);

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            String updateQuery = "UPDATE Users SET name = '" + newName + "' WHERE email = '" + user.getEmail() + "'";

            try {
                connectDB.createStatement().executeUpdate(updateQuery);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String newEmail = event.getNewValue();
            user.setEmail(newEmail);

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            String updateQuery = "UPDATE Users SET email = '" + newEmail + "' WHERE name = '" + user.getName() + "'";

            try {
                connectDB.createStatement().executeUpdate(updateQuery);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }




}
