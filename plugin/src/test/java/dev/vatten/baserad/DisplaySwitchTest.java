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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class DisplaySwitchTest {
    @Test void reloadKeepsLiteralTextAndLastValidRules(@TempDir Path dir) throws Exception {
        var rules = new DisplaySwitch();
        var warnings = new ArrayList<String>();
        rules.reload(dir, warnings::add);
        Path file = dir.resolve("display-switch.yml");
        assertTrue(Files.isRegularFile(file));
        assertTrue(rules.textNames().isEmpty());
        String config = "tags:\n  admin: {anzeige: text, text: '[Admin] '}\n  hidden: {anzeige: text, text: ''}\n  vip: {anzeige: grafik}\n";
        Files.writeString(file, config);
        rules.reload(dir, warnings::add);
        assertEquals(config, Files.readString(file));
        assertEquals("[Admin] ", rules.text("admin"));
        assertEquals("", rules.text("hidden"));
        assertNull(rules.text("vip"));
        assertTrue(warnings.isEmpty());
        for (String invalid : new String[]{"tags: [", "tags: {admin: {anzeige: text}}"}) {
            Files.writeString(file, invalid);
            rules.reload(dir, warnings::add);
            assertEquals("[Admin] ", rules.text("admin"));
        }
        assertEquals(2, warnings.size());
        Files.writeString(file, "tags: {}\n");
        rules.reload(dir, warnings::add);
        assertNull(rules.text("admin"));
    }
}
