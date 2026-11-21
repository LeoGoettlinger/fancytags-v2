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
import dev.vatten.baserad.symbols.AtlasSymbol;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;
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
        // The index and resource pack are generated together from the same badge artwork.
        var strips = new Properties();
        try (var in = TagsConfig.class.getResourceAsStream("/rank-sprites.properties")) {
            strips.load(Objects.requireNonNull(in, "Missing rank sprite index"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read rank sprite index", e);
        }
        for (String rank : new TreeSet<>(strips.stringPropertyNames())) {
            var symbols = new ArrayList<Symbol>();
            int widthInTiles = Integer.parseInt(strips.getProperty(rank));
            for (int i = 0; i < widthInTiles; i++) {
                symbols.add(new AtlasSymbol("minecraft:gui", "fancytags:ranks/" + rank + "/" + i));
            }
            tags.put(rank, new Tag(symbols));
        }
    }
}
