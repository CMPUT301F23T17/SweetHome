package com.example.sweethome;

/**
 * @class ReturnedItemData
 *
 * <p>This represents the item object with information returned by the barcode look api call.</p>
 *
 * @date <p>November 18, 2023</p>
 */
public class ReturnedItemData {
    private final String name;
    private final String description;
    private final String make;

    public ReturnedItemData(String name, String description, String make) {
        this.name = name;
        this.description = description;
        this.make = make;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMake() {
        return make;
    }
}
