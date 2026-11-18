package com.example.appfotoaustellung;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.appfotoaustellung.data.Area;
import com.example.appfotoaustellung.data.Exhibit;
import com.example.appfotoaustellung.data.ExhibitRepository;
import com.example.appfotoaustellung.data.ProgressStore;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

/**
 * Detailseite eines Stücks. Der Infotext erscheint erst, nachdem sich der Besucher
 * eine Weile Zeit für das Stück genommen hat (US-1). Gegenstücke werden verlinkt (US-2).
 * Wird auch direkt über den QR-Link kizausstellung://stueck/ID geöffnet (US-3).
 */
public class ExhibitActivity extends GuideActivity {

    public static final String EXTRA_EXHIBIT_ID = "exhibit_id";

    private static final String STATE_REVEALED = "revealed";
    private static final String STATE_REMAINING_MS = "remaining_ms";

    private ExhibitRepository repository;
    private Exhibit exhibit;
    private CountDownTimer timer;
    private long remainingMs;
    private boolean revealed;

    private View slowSection;
    private View infoSection;
    private CircularProgressIndicator slowProgress;
    private TextView slowCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        repository = ExhibitRepository.get(this);
        exhibit = repository.findById(readExhibitId(getIntent()));
        if (exhibit == null) {
            Toast.makeText(this, R.string.unknown_code, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (exhibit.isInteractive()) {
            // Interaktive Stücke direkt starten, auch wenn sie per Deep-Link kommen
            openExhibit(exhibit);
            finish();
            return;
        }

        setContentView(R.layout.activity_exhibit);
        EdgeToEdgeInsets.apply(findViewById(R.id.main));
        bindContent();

        remainingMs = exhibit.viewSeconds * 1000L;
        if (savedInstanceState != null) {
            revealed = savedInstanceState.getBoolean(STATE_REVEALED);
            remainingMs = savedInstanceState.getLong(STATE_REMAINING_MS, remainingMs);
        }
        if (revealed || remainingMs <= 0) {
            reveal(false);
        } else {
            startSlowLooking();
        }
    }

    private static String readExhibitId(Intent intent) {
        if (intent.getData() != null) {
            return QrLinks.parseExhibitId(intent.getData().toString());
        }
        return intent.getStringExtra(EXTRA_EXHIBIT_ID);
    }

    private void bindContent() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.station_format, repository.positionOf(exhibit)));
        Area area = repository.findArea(exhibit.areaId);
        if (area != null) toolbar.setSubtitle(area.title);
        toolbar.setNavigationOnClickListener(v -> finish());

        View placeholder = findViewById(R.id.image_placeholder);
        placeholder.setBackgroundColor(exhibit.colorInt());
        ((TextView) findViewById(R.id.text_placeholder_id)).setText(exhibit.id);

        ((TextView) findViewById(R.id.text_title)).setText(exhibit.title);
        ((TextView) findViewById(R.id.text_meta)).setText(
                getString(R.string.meta_format, exhibit.medium, exhibit.artist));
        ((TextView) findViewById(R.id.text_body)).setText(exhibit.text);
        ((TextView) findViewById(R.id.text_question)).setText(exhibit.question);

        slowSection = findViewById(R.id.section_slow);
        infoSection = findViewById(R.id.section_info);
        slowProgress = findViewById(R.id.progress_slow);
        slowCountdown = findViewById(R.id.text_countdown);
        findViewById(R.id.button_skip).setOnClickListener(v -> reveal(true));

        bindPair();
        bindNext();
    }

    private void bindPair() {
        MaterialCardView pairCard = findViewById(R.id.card_pair);
        Exhibit pair = exhibit.hasPair() ? repository.findById(exhibit.pairId) : null;
        if (pair == null) {
            pairCard.setVisibility(View.GONE);
            return;
        }
        ((TextView) findViewById(R.id.text_pair_title)).setText(pair.title);
        ((TextView) findViewById(R.id.text_pair_station)).setText(
                getString(R.string.station_format, repository.positionOf(pair)));
        pairCard.setOnClickListener(v -> openExhibit(pair));
    }

    private void bindNext() {
        MaterialButton next = findViewById(R.id.button_next);
        Exhibit nextExhibit = repository.next(exhibit);
        if (nextExhibit == null) {
            next.setText(R.string.tour_finish);
            next.setOnClickListener(v -> {
                Intent intent = new Intent(this, TourActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        } else {
            next.setText(getString(R.string.next_station_format,
                    repository.positionOf(nextExhibit), nextExhibit.title));
            next.setOnClickListener(v -> {
                openExhibit(nextExhibit);
                finish();
            });
        }
    }

    private void startSlowLooking() {
        slowSection.setVisibility(View.VISIBLE);
        infoSection.setVisibility(View.GONE);
        int totalMs = exhibit.viewSeconds * 1000;
        slowProgress.setMax(totalMs);

        timer = new CountDownTimer(remainingMs, 250) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMs = millisUntilFinished;
                slowProgress.setProgressCompat((int) (totalMs - millisUntilFinished), true);
                slowCountdown.setText(String.valueOf((millisUntilFinished + 999) / 1000));
            }

            @Override
            public void onFinish() {
                remainingMs = 0;
                reveal(true);
            }
        }.start();
    }

    private void reveal(boolean animate) {
        if (timer != null) timer.cancel();
        revealed = true;
        new ProgressStore(this).markVisited(exhibit.id);

        slowSection.setVisibility(View.GONE);
        infoSection.setVisibility(View.VISIBLE);
        if (animate) {
            infoSection.setAlpha(0f);
            infoSection.animate().alpha(1f).setDuration(600).start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_REVEALED, revealed);
        outState.putLong(STATE_REMAINING_MS, remainingMs);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }
}
