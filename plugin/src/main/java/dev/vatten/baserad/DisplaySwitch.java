/*
 *    Copyright 2026 vatten <vatten.dev>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package dev.vatten.baserad;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class DisplaySwitch {
    private static final String FILE = "display-switch.yml";
    private volatile Map<String, String> texts = Map.of();

    void reload(Path directory, Consumer<String> warn) {
        Path file = directory.resolve(FILE);
        try {
            if (Files.notExists(file)) {
                try (var template = Objects.requireNonNull(getClass().getResourceAsStream("/" + FILE))) {
                    Files.copy(template, file);
                }
            }
            try (var reader = Files.newBufferedReader(file)) {
                Object yaml = new Load(LoadSettings.builder().setLabel(FILE).build()).loadFromReader(reader);
                if (!(yaml instanceof Map<?, ?> root) || !root.containsKey("tags")) {
                    throw new IllegalArgumentException("Abschnitt 'tags:' fehlt oder hat die falsche Einrückung");
                }
                // A template with all examples commented out is a bare "tags:" (YAML null).
                Object section = root.get("tags");
                if (section == null) section = Map.of();
                if (!(section instanceof Map<?, ?> tags)) throw new IllegalArgumentException("'tags:' muss eine Zuordnung sein");
                Map<String, String> next = new HashMap<>();
                for (var entry : tags.entrySet()) {
                    if (entry.getKey() instanceof String name && entry.getValue() instanceof Map<?, ?> rule) {
                        if ("grafik".equals(rule.get("anzeige"))) continue;
                        if ("text".equals(rule.get("anzeige")) && rule.get("text") instanceof String text) {
                            next.put(name, text);
                            continue;
                        }
                    }
                    throw new IllegalArgumentException("Eintrag '" + entry.getKey() + "': 'anzeige: grafik' oder 'anzeige: text' mit Textwert erwartet");
                }
                texts = Map.copyOf(next);
            }
        } catch (IOException | YamlEngineException | IllegalArgumentException e) {
            warn.accept(file + ": " + e.getMessage() + ". Letzte gültige Anzeige bleibt aktiv.");
        }
    }

    String text(String name) { return texts.get(name); }
    Set<String> textNames() { return texts.keySet(); }
}
