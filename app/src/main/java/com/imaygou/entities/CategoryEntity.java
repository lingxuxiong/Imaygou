package com.imaygou.entities;

public class CategoryEntity {

    private final String name;
    private final String label;
    private final String icon;

    public CategoryEntity(String name, String label, String icon) {
        this.name = name;
        this.label = label;
        this.icon = icon;
    }

    public CategoryEntity(String name, String label) {
        this(name, label, null);
    }

    public CategoryEntity(String name) {
        this(name, null, null);
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }
}
