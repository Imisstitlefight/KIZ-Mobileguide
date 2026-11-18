package com.example.appfotoaustellung.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/** Merkt sich lokal, welche Stücke der Besucher schon angesehen hat. */
public class ProgressStore {

    private static final String PREFS = "rundgang";
    private static final String KEY_VISITED = "visited";

    private final SharedPreferences prefs;

    public ProgressStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isVisited(String exhibitId) {
        return prefs.getStringSet(KEY_VISITED, new HashSet<>()).contains(exhibitId);
    }

    public void markVisited(String exhibitId) {
        // Kopie anlegen, das Set aus getStringSet darf nicht direkt verändert werden
        Set<String> visited = new HashSet<>(prefs.getStringSet(KEY_VISITED, new HashSet<>()));
        if (visited.add(exhibitId)) {
            prefs.edit().putStringSet(KEY_VISITED, visited).apply();
        }
    }

    public int visitedCount() {
        return prefs.getStringSet(KEY_VISITED, new HashSet<>()).size();
    }

    public void reset() {
        prefs.edit().remove(KEY_VISITED).apply();
    }
}
