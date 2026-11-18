package com.example.appfotoaustellung;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.appfotoaustellung.data.Exhibit;
import com.example.appfotoaustellung.data.ExhibitRepository;
import com.example.appfotoaustellung.data.ProgressStore;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Interaktives Stück "Der Feed" (US-4): ein Feed zeigt im Sekundentakt Beiträge,
 * danach wird abgefragt, was hängen geblieben ist.
 */
public class InteractiveActivity extends GuideActivity {

    private static final long POST_DURATION_MS = 900;
    private static final int POST_COUNT = 20;

    private static final String[] POSTS = {
            "Nur heute: −50 %", "Du wirst nicht glauben, was dann passiert", "Neuer Trend!",
            "3 Tricks, die jeder kennen muss", "Jetzt live", "Gesponsert", "Das hat alles verändert",
            "Warte bis zum Ende", "Top 10 Momente", "Kommentiere mit 🔥", "Limitiert!",
            "Teile das mit deinen Freunden", "Viral gegangen", "Unglaublich", "Schnell sein lohnt sich",
            "Morgen schon vergessen", "Swipe für mehr", "Neue Folge", "Schau das an", "Exklusiv"
    };
    private static final int[] COLORS = {
            0xFFB23A48, 0xFFF29E4C, 0xFF3D6E8C, 0xFF5B8E7D, 0xFF8E5BA8, 0xFFE0B13A, 0xFF2F4F6F
    };

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final List<String> shownPosts = new ArrayList<>();

    private Exhibit exhibit;
    private View introSection;
    private View feedSection;
    private View quizSection;
    private View resultSection;
    private MaterialCardView postCard;
    private TextView postText;
    private TextView feedCounter;
    private int askedIndex;
    private long feedStartedAt;
    private long feedSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        exhibit = ExhibitRepository.get(this).findById(getIntent().getStringExtra(ExhibitActivity.EXTRA_EXHIBIT_ID));
        if (exhibit == null) {
            Toast.makeText(this, R.string.unknown_code, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_interactive);
        EdgeToEdgeInsets.apply(findViewById(R.id.main));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(exhibit.title);
        toolbar.setNavigationOnClickListener(v -> finish());

        introSection = findViewById(R.id.section_intro);
        feedSection = findViewById(R.id.section_feed);
        quizSection = findViewById(R.id.section_quiz);
        resultSection = findViewById(R.id.section_result);
        postCard = findViewById(R.id.card_post);
        postText = findViewById(R.id.text_post);
        feedCounter = findViewById(R.id.text_feed_counter);

        ((TextView) findViewById(R.id.text_intro)).setText(exhibit.text);
        findViewById(R.id.button_start_feed).setOnClickListener(v -> startFeed());
        findViewById(R.id.button_back_to_tour).setOnClickListener(v -> finish());

        showSection(introSection);
    }

    private void showSection(View section) {
        introSection.setVisibility(section == introSection ? View.VISIBLE : View.GONE);
        feedSection.setVisibility(section == feedSection ? View.VISIBLE : View.GONE);
        quizSection.setVisibility(section == quizSection ? View.VISIBLE : View.GONE);
        resultSection.setVisibility(section == resultSection ? View.VISIBLE : View.GONE);
    }

    private void startFeed() {
        shownPosts.clear();
        List<String> pool = new ArrayList<>();
        Collections.addAll(pool, POSTS);
        Collections.shuffle(pool, random);
        shownPosts.addAll(pool.subList(0, Math.min(POST_COUNT, pool.size())));

        showSection(feedSection);
        feedStartedAt = System.currentTimeMillis();
        showPost(0);
    }

    private void showPost(int index) {
        if (index >= shownPosts.size()) {
            showQuiz();
            return;
        }
        postText.setText(shownPosts.get(index));
        postCard.setCardBackgroundColor(COLORS[random.nextInt(COLORS.length)]);
        postText.setTextColor(Color.WHITE);
        feedCounter.setText(getString(R.string.feed_counter_format, index + 1, shownPosts.size()));
        postCard.setTranslationY(80f);
        postCard.setAlpha(0f);
        postCard.animate().translationY(0f).alpha(1f).setDuration(180).start();
        handler.postDelayed(() -> showPost(index + 1), POST_DURATION_MS);
    }

    private void showQuiz() {
        feedSeconds = (System.currentTimeMillis() - feedStartedAt) / 1000;
        showSection(quizSection);
        // Gefragt wird nach einem Beitrag aus der Mitte des Feeds
        askedIndex = shownPosts.size() / 2 + random.nextInt(3) - 1;
        String correct = shownPosts.get(askedIndex);

        List<String> options = new ArrayList<>();
        options.add(correct);
        List<String> others = new ArrayList<>(shownPosts);
        others.remove(correct);
        Collections.shuffle(others, random);
        options.add(others.get(0));
        options.add(others.get(1));
        Collections.shuffle(options, random);

        ((TextView) findViewById(R.id.text_quiz_question)).setText(
                getString(R.string.quiz_question_format, askedIndex + 1));
        int[] buttonIds = {R.id.button_option_1, R.id.button_option_2, R.id.button_option_3};
        for (int i = 0; i < buttonIds.length; i++) {
            MaterialButton button = findViewById(buttonIds[i]);
            String option = options.get(i);
            button.setText(option);
            button.setOnClickListener(v -> showResult(option.equals(correct), correct));
        }
    }

    private void showResult(boolean correct, String correctAnswer) {
        showSection(resultSection);
        new ProgressStore(this).markVisited(exhibit.id);

        ((TextView) findViewById(R.id.text_result_title)).setText(
                correct ? R.string.result_correct : R.string.result_wrong);
        ((TextView) findViewById(R.id.text_result_body)).setText(
                getString(R.string.result_body_format, shownPosts.size(), feedSeconds, correctAnswer));
        ((TextView) findViewById(R.id.text_result_question)).setText(exhibit.question);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
