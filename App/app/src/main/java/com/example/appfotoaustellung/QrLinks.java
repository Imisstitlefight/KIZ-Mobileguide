package com.example.appfotoaustellung;

import android.net.Uri;

/**
 * Format der QR-Codes an den Stücken: kizausstellung://stueck/ID, z. B. kizausstellung://stueck/A1.
 * Mit diesem Format öffnet auch die normale Handykamera direkt die App.
 * Zum Testen wird auch die reine ID ("A1") akzeptiert.
 */
public final class QrLinks {

    public static final String SCHEME = "kizausstellung";
    public static final String HOST = "stueck";

    private QrLinks() {
    }

    public static String linkFor(String exhibitId) {
        return SCHEME + "://" + HOST + "/" + exhibitId;
    }

    /** Liefert die Stück-ID aus dem QR-Inhalt oder null, wenn der Code nicht zur Ausstellung gehört. */
    public static String parseExhibitId(String content) {
        if (content == null) return null;
        String trimmed = content.trim();
        if (trimmed.isEmpty()) return null;

        Uri uri = Uri.parse(trimmed);
        if (SCHEME.equalsIgnoreCase(uri.getScheme()) && HOST.equalsIgnoreCase(uri.getHost())) {
            return uri.getLastPathSegment();
        }
        if (!trimmed.contains(":") && !trimmed.contains("/")) {
            return trimmed;
        }
        return null;
    }
}
