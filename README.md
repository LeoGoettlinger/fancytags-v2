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

Die mitgelieferten Standards basieren auf `fancytags-aktuell.json.gz`: 23 Rangpräfixe ohne Trenner und das reine TEAM-Suffix. Keine Clan-Tags. Die Schlüssel stehen in `plugins/FancyTags/display-switch.yml` (z. B. `%tags_co-owner%` für `owner-für-bob`, `%tags_team%` für `server-team`). Die Badges bestehen aus farbigen Buchstaben-Kopftexturen von [Minecraft Heads](https://minecraft-heads.com/), bezogen über den [Alphabet-Katalog](https://github.com/Random-MC/MinecraftHeads). Weder Ressourcenpaket noch eigener MineSkin-Schlüssel sind für diese Standards nötig.

**Grafik → Originaltext:** Entferne nur das erste `#` in der passenden Zeile der `display-switch.yml`, ohne Einrückung, Farben oder Leerzeichen zu ändern. `/tags reload` ausführen und beispielsweise mit `/papi parse me %tags_owner%` testen. Wieder auskommentieren schaltet zurück zur Grafik. PlaceholderAPI liefert den LuckPerms-Text mit seinen `&`-Codes; die Anzeige muss diese Farben unterstützen.

**Bestehende Installation:** Zuerst `tags.yml` und `display-switch.yml` sichern. Eigene Daten werden nicht absichtlich durch die neuen Vorlagen ersetzt. Um die frischen Standards zu übernehmen, bei gestopptem Server beide Dateien umbenennen, neu starten und eigene Tags bzw. Regeln aus der Sicherung ergänzen. Die Namen und Werte in LuckPerms werden nicht automatisch geändert.

LuckPerms kann für alle Server dasselbe Präfix oder Suffix speichern. Entscheidend ist, **wo es angezeigt wird**: TAB muss `%tags_admin%` auf dem jeweiligen Backend über PlaceholderAPI auswerten. Bei verschachtelten Platzhaltern (etwa `%luckperms-prefix%` mit `%tags_admin%` im Ergebnis) muss die Anzeige auch den inneren Placeholder auswerten. Teste bei Bedarf zuerst `%tags_admin%` direkt in TAB. Für Grafiken muss die Anzeige außerdem die von FancyTags ausgegebene MiniMessage-Darstellung unterstützen. FancyTags synchronisiert selbst keine Werte und ändert nichts in LuckPerms oder TAB.

**Grafikstand:** Für `owner`, `admin`, `dev`, `mod` und `builder` sind die vollständigen signierten Originalgrafiken aus FancyTags hinterlegt. Für die übrigen, neu hinzugekommenen Rangnamen werden weiterhin Buchstaben-Köpfe verwendet. Die Textvarianten in `display-switch.yml` sind davon unabhängig.
