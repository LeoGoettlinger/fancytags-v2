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

package dev.vatten.baserad.configs;

import de.exlll.configlib.Configuration;
import dev.vatten.baserad.Tag;
import dev.vatten.baserad.symbols.Symbol;
import dev.vatten.baserad.symbols.PlayerSymbol;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.Map;

@Configuration
public class TagsConfig {
    @Getter
    @Setter
    private Map<String, Tag> tags;

    {
        tags = new LinkedHashMap<>();
        OriginalRankBadges.addTo(tags);
        badge("co-owner", "Light Blue", "CO-OWNER");
        badge("team-leitung", "Orange", "TEAM LEITUNG");
        badge("dev-leitung", "Light Blue", "DEV LEITUNG");
        badge("co-admin", "Red", "CO ADMIN");
        badge("mod-plus", "Blue", "MOD +");
        badge("sup-plus", "Yellow", "SUP +");
        badge("partner", "Yellow", "PARTNER");
        badge("sup", "Yellow", "SUP");
        badge("media", "Pink", "MEDIA");
        badge("netherite", "Black", "NETHERITE");
        badge("amethyst", "Purple", "AMETHYST");
        badge("og-rang", "Orange", "OG");
        badge("diamond", "Cyan", "DIAMOND");
        badge("gold", "Yellow", "GOLD");
        badge("eisen", "White", "EISEN");
        badge("stone", "Light Gray", "STONE");
        badge("leather", "Gray", "LEATHER");
        badge("spieler", "Light Gray", "SPIELER");
        badge("team", "Lime", "TEAM");
    }

    private static final Properties LETTERS = loadLetters();

    private static Properties loadLetters() {
        Properties letters = new Properties();
        try (var in = TagsConfig.class.getResourceAsStream("/badge-letters.properties")) {
            letters.load(Objects.requireNonNull(in, "Missing badge alphabet"));
            return letters;
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private void badge(String name, String palette, String label) {
        var symbols = new ArrayList<Symbol>();
        for (char ch : label.toCharArray()) {
            String glyph = switch (ch) {
                case ' ' -> "Blank";
                case '-' -> "Minus";
                case '+' -> "Plus";
                default -> String.valueOf(ch);
            };
            String key = palette.replace(' ', '_') + "." + glyph;
            symbols.add(new PlayerSymbol(Objects.requireNonNull(LETTERS.getProperty(key), key), null));
        }
        tags.put(name, new Tag(symbols));
    }
}
