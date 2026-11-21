# FancyTags

Paper-Zielversionen: **26.2 und 26.3** (Java 25). Dasselbe Plugin-JAR wird gegen die ältere API gebaut.

```sh
./gradlew :paper:shadowJar
./gradlew :paper:runServer -PpaperVersion=26.2
./gradlew :paper:runServer -PpaperVersion=26.3
```

Das Plugin liegt nach dem Build unter `paper/build/libs/`. Die Testserver verwenden getrennte Verzeichnisse unter `paper/run/`.


## Fetch Template Changes
[From answer on stackoverflow](https://stackoverflow.com/questions/37471740/how-to-copy-commits-from-one-git-repo-to-another)
```
git remote add baserad https://github.com/Vatten/baserad
git remote update
git cherry-pick <sha-of-commit>
git push origin main
git remote remove baserad
```
## Gleicher Rang, andere Anzeige pro Server

Die mitgelieferten Standards basieren auf `fancytags-aktuell.json.gz`: 23 Rangpräfixe ohne Trenner und das reine TEAM-Suffix. Keine Clan-Tags. Die Schlüssel stehen in `plugins/FancyTags/display-switch.yml` (z. B. `%tags_co-owner%` für `owner-für-bob`, `%tags_team%` für `server-team`). OWNER behält seine fünf unveränderten Original-Kopftexturen. Alle anderen Badges sind je ein neu gezeichneter, durchgehender 8-Pixel-Schriftzug in der Rangfarbe. Daraus werden quadratische GUI-Sprites für Minecraft erzeugt. [Grafikvorschau](artwork/Rang-Vorschau.png).

### Ressourcenpaket für die 23 neuen Grafiken

Der Actions-Download enthält **die Plugin-JAR und `FancyTags-Ranks.zip`**. Nur die JAR kommt in `plugins/`. Das innere ZIP ist ein Minecraft-Ressourcenpaket für 26.2/26.3. Für einen lokalen Test kannst du es im Client unter Ressourcenpakete aktivieren. Für alle Spieler trägst du auf dem Server eine direkt erreichbare HTTPS-URL zum **inneren ZIP** in `server.properties` ein und startest neu. Beispiel für diesen Build:

```properties
resource-pack=https://raw.githubusercontent.com/LeoGoettlinger/fancytags-v2/arena/ec44b073-fancytags-v2/artwork/FancyTags-Ranks.zip
resource-pack-sha1=012de9d6da6cd8079141a65ebf96b0bb3f749df8
```

Spieler müssen das Paket annehmen. Bei einem privaten Repository das ZIP selbst öffentlich hosten; für einen dauerhaft festen Stand kann der Branchname in der URL durch die Build-Commit-ID ersetzt werden. Bei vorhandenen Server-Packs die `assets/fancytags/`-Dateien integrieren, statt das bestehende Pack zu ersetzen; dann dessen eigenen SHA-1 verwenden. Ohne geladenes Pack funktionieren OWNER und die Textvarianten, die neuen GUI-Sprites fehlen dann beim Client. Kein MineSkin-Schlüssel nötig.

**Grafik → Originaltext:** Entferne nur das erste `#` in der passenden Zeile der `display-switch.yml`, ohne Einrückung, Farben oder Leerzeichen zu ändern. `/tags reload` ausführen und beispielsweise mit `/papi parse me %tags_owner%` testen. Wieder auskommentieren schaltet zurück zur Grafik. PlaceholderAPI liefert den LuckPerms-Text mit seinen `&`-Codes; die Anzeige muss diese Farben unterstützen.

**Bestehende Installation:** Zuerst `tags.yml` und `display-switch.yml` sichern. Eigene Daten werden nicht absichtlich durch die neuen Vorlagen ersetzt. Um die frischen Standards zu übernehmen, bei gestopptem Server beide Dateien umbenennen, neu starten und eigene Tags bzw. Regeln aus der Sicherung ergänzen. Die Namen und Werte in LuckPerms werden nicht automatisch geändert.

LuckPerms kann für alle Server dasselbe Präfix oder Suffix speichern. Entscheidend ist, **wo es angezeigt wird**: TAB muss `%tags_admin%` auf dem jeweiligen Backend über PlaceholderAPI auswerten. Bei verschachtelten Platzhaltern (etwa `%luckperms-prefix%` mit `%tags_admin%` im Ergebnis) muss die Anzeige auch den inneren Placeholder auswerten. Teste bei Bedarf zuerst `%tags_admin%` direkt in TAB. Für Grafiken muss die Anzeige außerdem die von FancyTags ausgegebene MiniMessage-Darstellung unterstützen. FancyTags synchronisiert selbst keine Werte und ändert nichts in LuckPerms oder TAB.
