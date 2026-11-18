package com.example.appfotoaustellung.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Lädt Bereiche und Stücke einmalig aus assets/exhibits.json. */
public class ExhibitRepository {

    private static ExhibitRepository instance;

    private final List<Area> areas = new ArrayList<>();
    private final List<Exhibit> exhibits = new ArrayList<>();

    public static synchronized ExhibitRepository get(Context context) {
        if (instance == null) {
            instance = new ExhibitRepository(context.getApplicationContext());
        }
        return instance;
    }

    private ExhibitRepository(Context context) {
        try (InputStream in = context.getAssets().open("exhibits.json")) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, read);
            }
            String json = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(json);

            JSONArray areaArray = root.getJSONArray("areas");
            for (int i = 0; i < areaArray.length(); i++) {
                JSONObject a = areaArray.getJSONObject(i);
                areas.add(new Area(a.getString("id"), a.getString("title"), a.optString("description")));
            }

            JSONArray exhibitArray = root.getJSONArray("exhibits");
            for (int i = 0; i < exhibitArray.length(); i++) {
                JSONObject e = exhibitArray.getJSONObject(i);
                exhibits.add(new Exhibit(
                        e.getString("id"),
                        e.getString("area"),
                        e.getString("title"),
                        e.optString("artist"),
                        e.optString("medium"),
                        e.optString("short"),
                        e.optString("text"),
                        e.optString("question"),
                        e.optInt("viewSeconds", 0),
                        e.optString("pairId"),
                        e.optString("type", Exhibit.TYPE_INFO),
                        e.optString("color")));
            }
        } catch (IOException | JSONException e) {
            throw new IllegalStateException("exhibits.json konnte nicht gelesen werden", e);
        }
    }

    public List<Area> getAreas() {
        return Collections.unmodifiableList(areas);
    }

    /** Alle Stücke in Rundgang-Reihenfolge. */
    public List<Exhibit> getExhibits() {
        return Collections.unmodifiableList(exhibits);
    }

    public Exhibit findById(String id) {
        if (id == null) return null;
        for (Exhibit e : exhibits) {
            if (e.id.equalsIgnoreCase(id.trim())) return e;
        }
        return null;
    }

    public Area findArea(String areaId) {
        for (Area a : areas) {
            if (a.id.equals(areaId)) return a;
        }
        return null;
    }

    /** Nächstes Stück im Rundgang oder null am Ende. */
    public Exhibit next(Exhibit current) {
        int index = exhibits.indexOf(current);
        if (index < 0 || index + 1 >= exhibits.size()) return null;
        return exhibits.get(index + 1);
    }

    public int positionOf(Exhibit exhibit) {
        return exhibits.indexOf(exhibit) + 1;
    }
}
