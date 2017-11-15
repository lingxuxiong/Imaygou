package com.imaygou.entities;

import java.util.List;

public class SubcategoryEntity {

    public static final String GRID_FORMAT = "grid";
    public static final String LIST_FORMAT = "list";

    private final String title;
    private final List<CategoryEntity> items;
    private final String itemsDisplayFormat;

    public SubcategoryEntity(String title, List<CategoryEntity> items, String format) {
        this.title = title;
        this.items = items;
        this.itemsDisplayFormat = format;
    }

    public String getTitle() {
        return title;
    }

    public List<CategoryEntity> getItems() {
        return items;
    }

    public String getItemsDisplayFormat() {
        return itemsDisplayFormat;
    }
}
