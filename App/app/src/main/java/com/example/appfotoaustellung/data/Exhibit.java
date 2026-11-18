package com.example.appfotoaustellung.data;

import android.graphics.Color;

/** Ein Ausstellungsstück, wie es in assets/exhibits.json beschrieben ist. */
public class Exhibit {
    public static final String TYPE_INFO = "info";
    public static final String TYPE_INTERACTIVE = "interactive";

    public final String id;
    public final String areaId;
    public final String title;
    public final String artist;
    public final String medium;
    public final String shortText;
    public final String text;
    public final String question;
    /** Wie lange der Besucher das Stück betrachten soll, bevor der Infotext erscheint (US-1). */
    public final int viewSeconds;
    /** Id des gegenübergestellten Stücks (US-2), leer wenn es keines gibt. */
    public final String pairId;
    public final String type;
    public final String color;

    public Exhibit(String id, String areaId, String title, String artist, String medium,
                   String shortText, String text, String question, int viewSeconds,
                   String pairId, String type, String color) {
        this.id = id;
        this.areaId = areaId;
        this.title = title;
        this.artist = artist;
        this.medium = medium;
        this.shortText = shortText;
        this.text = text;
        this.question = question;
        this.viewSeconds = viewSeconds;
        this.pairId = pairId;
        this.type = type;
        this.color = color;
    }

    public boolean isInteractive() {
        return TYPE_INTERACTIVE.equals(type);
    }

    public boolean hasPair() {
        return pairId != null && !pairId.isEmpty();
    }

    /** Platzhalterfarbe, solange es noch keine echten Fotos gibt. */
    public int colorInt() {
        try {
            return Color.parseColor(color);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Color.DKGRAY;
        }
    }
}
