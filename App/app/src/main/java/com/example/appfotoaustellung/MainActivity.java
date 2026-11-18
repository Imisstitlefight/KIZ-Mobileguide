package com.example.appfotoaustellung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import com.example.appfotoaustellung.data.ExhibitRepository;
import com.example.appfotoaustellung.data.ProgressStore;

/** Startseite: Begrüßung, kurze Erklärung und Einstieg in den Rundgang (US-5). */
public class MainActivity extends GuideActivity {

    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        EdgeToEdgeInsets.apply(findViewById(R.id.main));

        progressText = findViewById(R.id.text_progress);
        findViewById(R.id.button_start_tour).setOnClickListener(v ->
                startActivity(new Intent(this, TourActivity.class)));
        findViewById(R.id.button_scan).setOnClickListener(v -> startQrScan());
        findViewById(R.id.button_manual).setOnClickListener(v -> showManualCodeDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        int visited = new ProgressStore(this).visitedCount();
        int total = ExhibitRepository.get(this).getExhibits().size();
        if (visited == 0) {
            progressText.setText(R.string.progress_none);
        } else {
            progressText.setText(getString(R.string.progress_format, visited, total));
        }
    }
}
