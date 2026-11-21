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

Die mitgelieferten Standards basieren auf `fancytags-aktuell.json.gz`: 23 Rangpräfixe ohne Trenner und das reine TEAM-Suffix. Keine Clan-Tags. Die Schlüssel stehen in `plugins/FancyTags/display-switch.yml` (z. B. `%tags_co-owner%` für `owner-für-bob`, `%tags_team%` für `server-team`). OWNER behält seine fünf unveränderten Original-Kopftexturen. Alle anderen Badges sind je ein neu gezeichneter, durchgehender 8-Pixel-Schriftzug in der Rangfarbe. Alle Kacheln werden als normale Minecraft-Spielerköpfe dargestellt. [Grafikvorschau](artwork/Rang-Vorschau.png).

### Installation ohne Ressourcenpaket

Nur die JAR aus dem erfolgreichen Paper-Build nach `plugins/` kopieren. Spieler benötigen weder ein Ressourcenpaket noch Mods; ihr Minecraft-Client lädt die Kopftexturen wie bei OWNER vom normalen Minecraft-Texturserver. Auch auf dem Server ist dafür **kein MineSkin-Schlüssel** nötig. Die zuletzt ausgelieferten `fancytags:ranks/...`-Sprites werden auch in bestehenden `tags.yml` beim Anzeigen automatisch durch die entsprechenden Köpfe ersetzt. Eigene Sprites anderer Packs bleiben unberührt. Wenn du ausschließlich das alte FancyTags-Pack in `server.properties` eingetragen hattest, kannst du dessen Einträge entfernen; andere Server-Packs nicht abschalten.

`artwork/FancyTags-Ranks.zip` ist nur noch die verlustfreie **Build-Vorlage**, kein Client-Download. `tools/badges/sign_heads.py` setzt jede 8×8-Kachel unskaliert auf ein 64×64-Skin, prüft die heruntergegebene Mojang-Kopftextur Pixel für Pixel und speichert die signierten Profile für die JAR. Wiederholte Kacheln werden nur einmal signiert. Die normale Plugin-Ausführung braucht keine Verbindung zu MineSkin.

**Grafik → Originaltext:** Entferne nur das erste `#` in der passenden Zeile der `display-switch.yml`, ohne Einrückung, Farben oder Leerzeichen zu ändern. `/tags reload` ausführen und beispielsweise mit `/papi parse me %tags_owner%` testen. Wieder auskommentieren schaltet zurück zur Grafik. PlaceholderAPI liefert den LuckPerms-Text mit seinen `&`-Codes; die Anzeige muss diese Farben unterstützen.

**Bestehende Installation:** Zuerst `tags.yml` und `display-switch.yml` sichern. Eigene Daten werden nicht absichtlich durch die neuen Vorlagen ersetzt. Um die frischen Standards zu übernehmen, bei gestopptem Server beide Dateien umbenennen, neu starten und eigene Tags bzw. Regeln aus der Sicherung ergänzen. Die Namen und Werte in LuckPerms werden nicht automatisch geändert.

LuckPerms kann für alle Server dasselbe Präfix oder Suffix speichern. Entscheidend ist, **wo es angezeigt wird**: TAB muss `%tags_admin%` auf dem jeweiligen Backend über PlaceholderAPI auswerten. Bei verschachtelten Platzhaltern (etwa `%luckperms-prefix%` mit `%tags_admin%` im Ergebnis) muss die Anzeige auch den inneren Placeholder auswerten. Teste bei Bedarf zuerst `%tags_admin%` direkt in TAB. Für Grafiken muss die Anzeige außerdem die von FancyTags ausgegebene MiniMessage-Darstellung unterstützen. FancyTags synchronisiert selbst keine Werte und ändert nichts in LuckPerms oder TAB.
