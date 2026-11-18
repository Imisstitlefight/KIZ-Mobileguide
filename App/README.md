# App-Guide „Nimm dir Zeit“ (Prototyp)

Android-App (Java) als Guide für die Multimedia-Ausstellung im KIZ Ybbs.
Prototyp zu SCRUM-18 („Technik für App-Guide und QR-Codes wählen und Prototyp testen“).

## Was der Prototyp kann

| User Story | Umsetzung |
|---|---|
| US-1 Bewusst Zeit nehmen | Detailseite zeigt erst einen Countdown (`viewSeconds`), dann den Infotext und eine Reflexionsfrage |
| US-2 Analog und digital vergleichen | Stücke mit `pairId` verlinken auf ihr Gegenstück |
| US-3 Per QR-Code mehr erfahren | QR-Scanner in der App (ZXing) + Deep-Link `kizausstellung://stueck/ID`, öffnet auch über die normale Kamera-App |
| US-4 Interaktion auslösen | Interaktives Stück „Der Feed“ (I1): schneller Feed, danach Merk-Quiz |
| US-5 Durch die Ausstellung geführt werden | Startseite, Rundgang nach Bereichen, „Weiter zu Station X“, Fortschritt mit Häkchen |

## Inhalte bearbeiten

Alle Stücke und Bereiche stehen in `app/src/main/assets/exhibits.json`.
Reihenfolge der Stücke = Reihenfolge im Rundgang. Die Texte sind Platzhalter (SCRUM-25 / SCRUM-34).

## Testen

- In Android Studio öffnen und auf Gerät/Emulator starten.
- QR-Codes: `docs/qr-testcodes.html` im Browser öffnen und mit der App scannen.
- Ohne Kamera: „Code eingeben“ und z. B. `A1` eintippen, oder per adb:
  `adb shell am start -a android.intent.action.VIEW -d "kizausstellung://stueck/A1"`

## Nächste Schritte

- Echte Fotos statt Farb-Platzhalter (Bilder in `res/drawable` oder `assets`, Feld `image` im JSON)
- Echte Infotexte und Stückliste aus SCRUM-15 / SCRUM-26
- Test mit Testpersonen (SCRUM-36)
