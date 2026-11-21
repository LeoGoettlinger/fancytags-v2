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

package dev.vatten.baserad.symbols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;

/** The shipped texture values are immutable Mojang profiles. No signing service is used at runtime. */
public final class BundledHeads {
    private BundledHeads() {}
    private record Texture(String value, String signature) {
        PlayerSymbol symbol() { return new PlayerSymbol(value, signature); }
    }
    private static final Map<String, List<Texture>> RANKS = load();

    private static Properties read(String name) {
        var result = new Properties();
        try (var in = BundledHeads.class.getResourceAsStream("/" + name)) {
            result.load(Objects.requireNonNull(in, "Missing " + name));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + name, e);
        }
        return result;
    }

    private static Map<String, List<Texture>> load() {
        var ranks = new TreeMap<String, List<Texture>>();
        var textures = read("head-textures.properties");
        var strips = read("rank-heads.properties");
        for (String rank : strips.stringPropertyNames()) {
            var tiles = new ArrayList<Texture>();
            for (String hash : strips.getProperty(rank).split(",")) {
                String[] pair = Objects.requireNonNull(textures.getProperty(hash), hash).split(",", -1);
                if (pair.length != 2 || pair[0].isEmpty() || pair[1].isEmpty())
                    throw new IllegalStateException("Incomplete signed tile for " + rank);
                tiles.add(new Texture(pair[0], pair[1]));
            }
            ranks.put(rank, List.copyOf(tiles));
        }
        return ranks;
    }

    public static void addDefaults(Map<String, dev.vatten.baserad.Tag> tags) {
        RANKS.forEach((rank, tiles) -> tags.put(rank,
                new dev.vatten.baserad.Tag(tiles.stream().<Symbol>map(Texture::symbol).toList())));
    }

    /** Recognize only sprites shipped by the previous version; leave custom sprites untouched. */
    static PlayerSymbol legacyTile(String atlas, String sprite) {
        if (!"minecraft:gui".equals(atlas) || !sprite.startsWith("fancytags:ranks/")) return null;
        String[] path = sprite.substring("fancytags:ranks/".length()).split("/", -1);
        if (path.length != 2 || !RANKS.containsKey(path[0])) return null;
        try {
            int index = Integer.parseInt(path[1]);
            var tiles = RANKS.get(path[0]);
            if (index >= 0 && index < tiles.size() && Integer.toString(index).equals(path[1])) return tiles.get(index).symbol();
        } catch (NumberFormatException ignored) { }
        return null;
    }
}
