package com.example.appfotoaustellung;

import android.content.Intent;
import android.text.InputType;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appfotoaustellung.data.Exhibit;
import com.example.appfotoaustellung.data.ExhibitRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/** Gemeinsame Basis: QR-Code scannen, Code manuell eingeben und Stücke öffnen. */
public abstract class GuideActivity extends AppCompatActivity {

    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    handleCode(result.getContents());
                }
            });

    protected void startQrScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt(getString(R.string.scan_prompt));
        options.setBeepEnabled(false);
        options.setOrientationLocked(false);
        scanLauncher.launch(options);
    }

    /** Fallback ohne Kamera (z. B. im Emulator oder wenn ein Code beschädigt ist). */
    protected void showManualCodeDialog() {
        EditText input = new EditText(this);
        input.setHint(R.string.manual_code_hint);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        FrameLayout container = new FrameLayout(this);
        int padding = getResources().getDimensionPixelSize(R.dimen.space_l);
        container.setPadding(padding, 0, padding, 0);
        container.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.manual_code_title)
                .setView(container)
                .setPositiveButton(R.string.open, (d, w) -> handleCode(input.getText().toString()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    protected void handleCode(String content) {
        String id = QrLinks.parseExhibitId(content);
        Exhibit exhibit = ExhibitRepository.get(this).findById(id);
        if (exhibit == null) {
            Toast.makeText(this, R.string.unknown_code, Toast.LENGTH_LONG).show();
            return;
        }
        openExhibit(exhibit);
    }

    protected void openExhibit(Exhibit exhibit) {
        Class<?> target = exhibit.isInteractive() ? InteractiveActivity.class : ExhibitActivity.class;
        Intent intent = new Intent(this, target);
        intent.putExtra(ExhibitActivity.EXTRA_EXHIBIT_ID, exhibit.id);
        startActivity(intent);
    }
}
