package com.example.appfotoaustellung;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

final class EdgeToEdgeInsets {

    private EdgeToEdgeInsets() {
    }

    /** Polstert die Wurzel-View um die Systemleisten, damit nichts darunter verschwindet. */
    static void apply(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
