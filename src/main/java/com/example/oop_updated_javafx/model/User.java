package com.example.oop_updated_javafx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;

public class User {
    private SimpleStringProperty name;
    private SimpleStringProperty email;

    public User(User user) {
        this.name = new SimpleStringProperty(user.getName());
        this.email = new SimpleStringProperty(user.getEmail());
    }
    public User(final String name, final String email) {
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
    }

    public String getName() {
        return name.get();
    }

    public void setName(final String name) {
        this.name.set(name);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(final String email) {
        this.email.set(email);
    }

    public ObservableValue<String> nameProperty() {
        return name;
    }

    public ObservableValue<String> emailProperty() {
        return email;
    }
}
