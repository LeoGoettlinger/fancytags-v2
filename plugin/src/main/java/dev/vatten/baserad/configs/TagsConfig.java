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
import dev.vatten.baserad.symbols.BundledHeads;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class TagsConfig {
    @Getter
    @Setter
    private Map<String, Tag> tags;

    {
        tags = new LinkedHashMap<>();
        OriginalRankBadges.addTo(tags);
        BundledHeads.addDefaults(tags);
    }
}
