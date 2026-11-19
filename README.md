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