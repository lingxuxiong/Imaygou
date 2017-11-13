package com.imaygou.data;

public class Item {

    private final String id;
    private final String name;

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}
