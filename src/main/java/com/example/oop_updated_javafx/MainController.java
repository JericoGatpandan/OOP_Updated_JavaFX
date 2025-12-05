package com.example.oop_updated_javafx;

import com.example.oop_updated_javafx.model.DatabaseConnection;
import com.example.oop_updated_javafx.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void onAddUser() {
        if (!nameField.getText().isBlank() && !emailField.getText().isBlank()) {
            String name = nameField.getText();
            String email = emailField.getText();

            insertUser(name, email);

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

    public void onEditUser() {
        User selected = TableContainer.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user in the table to edit.");
            alert.showAndWait();
            return;
        }

        if (nameField.getText().isBlank() || emailField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in both Name and Email fields to edit.");
            alert.showAndWait();
            return;
        }

        String oldName = selected.getName();
        String oldEmail = selected.getEmail();
        String newName = nameField.getText();
        String newEmail = emailField.getText();

        boolean success = updateUserInDatabase(oldName, oldEmail, newName, newEmail);
        if (success) {
            selected.setName(newName);
            selected.setEmail(newEmail);
            nameField.clear();
            emailField.clear();
            buildTable();
        }
    }

    public void onDeleteUser() {
        User selected = TableContainer.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user in the table to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete User");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected user?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String deleteQuery = "DELETE FROM Users WHERE name = '" + selected.getName() + "' AND email = '" + selected.getEmail() + "'";

        try {
            connectDB.createStatement().executeUpdate(deleteQuery);
            buildTable();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while deleting the user.");
            alert.showAndWait();
        }
    }

    // Helper to insert a new user into the database
    private void insertUser(String name, String email) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String insertValues = "INSERT INTO Users(name, email) VALUES ('" + name + "','" + email + "')";

        try {
            connectDB.createStatement().executeUpdate(insertValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper to update a user in the database based on old values
    private boolean updateUserInDatabase(String oldName, String oldEmail, String newName, String newEmail) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String updateQuery = "UPDATE Users SET name = '" + newName + "', email = '" + newEmail + "' WHERE name = '" + oldName + "' AND email = '" + oldEmail + "'";

        try {
            int affected = connectDB.createStatement().executeUpdate(updateQuery);
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while updating the user.");
            alert.showAndWait();
            return false;
        }
    }

    public void buildTable() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String UserViewQuery = "SELECT name, email FROM Users";

        ObservableList<User> data = FXCollections.observableArrayList();

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
            String oldName = user.getName();
            String oldEmail = user.getEmail();
            String newName = event.getNewValue();

            boolean success = updateUserInDatabase(oldName, oldEmail, newName, oldEmail);
            if (success) {
                user.setName(newName);
            } else {
                // revert change in UI
                TableContainer.refresh();
            }
        });

        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String oldName = user.getName();
            String oldEmail = user.getEmail();
            String newEmail = event.getNewValue();

            boolean success = updateUserInDatabase(oldName, oldEmail, oldName, newEmail);
            if (success) {
                user.setEmail(newEmail);
            } else {
                TableContainer.refresh();
            }
        });
    }
}
