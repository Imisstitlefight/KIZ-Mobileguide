package com.example.appfotoaustellung.data;

/** Ein Ausstellungsbereich (z. B. Analog, Digital). */
public class Area {
    public final String id;
    public final String title;
    public final String description;

    public Area(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}
