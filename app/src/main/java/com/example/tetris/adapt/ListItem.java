package com.example.tetris.adapt;

public class ListItem {
    private String description;
    private String date;
    private String username;

    public ListItem(String description, String date, String username) {
        this.description = description;
        this.date = date;
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
