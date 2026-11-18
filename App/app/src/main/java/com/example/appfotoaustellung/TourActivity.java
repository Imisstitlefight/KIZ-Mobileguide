package com.example.appfotoaustellung;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfotoaustellung.data.ExhibitRepository;
import com.example.appfotoaustellung.data.ProgressStore;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

/** Rundgang: alle Bereiche und Stücke in Reihenfolge, mit Fortschritt (US-5). */
public class TourActivity extends GuideActivity {

    private ExhibitRepository repository;
    private ProgressStore progressStore;
    private TourAdapter adapter;
    private TextView progressText;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tour);
        EdgeToEdgeInsets.apply(findViewById(R.id.main));

        repository = ExhibitRepository.get(this);
        progressStore = new ProgressStore(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_manual_code) {
                showManualCodeDialog();
                return true;
            } else if (item.getItemId() == R.id.action_reset) {
                confirmReset();
                return true;
            }
            return false;
        });

        progressText = findViewById(R.id.text_progress);
        progressBar = findViewById(R.id.progress_tour);

        RecyclerView list = findViewById(R.id.list_exhibits);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TourAdapter(repository, progressStore, this::openExhibit);
        list.setAdapter(adapter);

        findViewById(R.id.fab_scan).setOnClickListener(v -> startQrScan());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProgress();
        adapter.notifyDataSetChanged();
    }

    private void updateProgress() {
        int total = repository.getExhibits().size();
        int visited = progressStore.visitedCount();
        progressBar.setMax(total);
        progressBar.setProgressCompat(visited, true);
        progressText.setText(visited >= total
                ? getString(R.string.progress_done)
                : getString(R.string.progress_format, visited, total));
    }

    private void confirmReset() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reset_title)
                .setMessage(R.string.reset_message)
                .setPositiveButton(R.string.reset_confirm, (d, w) -> {
                    progressStore.reset();
                    updateProgress();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
