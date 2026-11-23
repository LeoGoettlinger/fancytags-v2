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

import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The shipped jar must reproduce the signed profiles exactly, without any signing service at runtime. */
class BundledHeadsTest {
    private static Properties load(String name) throws Exception {
        var answer = new Properties();
        try (var in = BundledHeadsTest.class.getResourceAsStream("/" + name)) {
            assertNotNull(in, "missing " + name);
            answer.load(in);
        }
        return answer;
    }

    @Test void everyHeadCarriesTheBundledSignedProfile() throws Exception {
        var textures = load("head-textures.properties");
        var strips = load("rank-heads.properties");
        var widths = load("rank-sprites.properties");
        var badges = new dev.vatten.baserad.configs.TagsConfig().getTags();
        var mini = MiniMessage.miniMessage();

        assertEquals(122, textures.size());
        assertEquals(23, strips.size());
        assertEquals(strips.stringPropertyNames(), widths.stringPropertyNames());

        int tiles = 0;
        for (String rank : strips.stringPropertyNames()) {
            String[] hashes = strips.getProperty(rank).split(",");
            var heads = badges.get(rank).serialize(mini).children();
            assertEquals(Integer.parseInt(widths.getProperty(rank)), hashes.length, rank);
            assertEquals(hashes.length, heads.size(), rank);
            for (int i = 0; i < hashes.length; i++) {
                tiles++;
                var contents = ((ObjectComponent) heads.get(i)).contents();
                assertTrue(contents instanceof PlayerHeadObjectContents, rank + "/" + i);
                var properties = ((PlayerHeadObjectContents) contents).profileProperties();
                assertEquals(1, properties.size(), rank + "/" + i);
                var property = properties.iterator().next();
                assertEquals("textures", property.name());
                String[] pair = textures.getProperty(hashes[i]).split(",", -1);
                assertEquals(pair[0], property.value(), "value " + rank + "/" + i);
                assertEquals(pair[1], property.signature(), "signature " + rank + "/" + i);
            }
        }
        assertEquals(133, tiles);
    }
}
