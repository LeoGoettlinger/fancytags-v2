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

FancyTags legt auf jedem Paper-Server `plugins/FancyTags/display-switch.yml` an. Ohne Änderung bleiben alle Tags grafisch. Für ein Textpräfix auf diesem Server ersetze `tags: {}` in **dieser Datei** durch:

```yaml
tags:
  admin:
    anzeige: text
    text: "[Admin] "
  vip:
    anzeige: grafik
```

Speichern und als berechtigter Spieler `/tags reload` ausführen. Test: `/papi parse me %tags_admin%` sollte jetzt `[Admin] ` liefern. Für die Grafik den Eintrag entfernen oder `anzeige: grafik` setzen; dazu muss der grafische Tag auf diesem Server existieren. Die Tag-Namen müssen exakt übereinstimmen. Texte werden von FancyTags nicht als weitere Placeholder ausgewertet.

LuckPerms kann für alle Server dasselbe Präfix oder Suffix speichern. Entscheidend ist, **wo es angezeigt wird**: TAB muss `%tags_admin%` auf dem jeweiligen Backend über PlaceholderAPI auswerten. Bei verschachtelten Platzhaltern (etwa `%luckperms-prefix%` mit `%tags_admin%` im Ergebnis) muss die Anzeige auch den inneren Placeholder auswerten. Teste bei Bedarf zuerst `%tags_admin%` direkt in TAB. Für Grafiken muss die Anzeige außerdem die von FancyTags ausgegebene MiniMessage-Darstellung unterstützen. FancyTags synchronisiert selbst keine Werte und ändert nichts in LuckPerms oder TAB.
