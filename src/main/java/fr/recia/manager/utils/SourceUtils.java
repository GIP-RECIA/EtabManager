/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.recia.manager.utils;

import java.util.Objects;

import static fr.recia.manager.configuration.Constants.SARAPISUI_;

public class SourceUtils {

    private SourceUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isSourceOfficial(String source) {
        return !source.startsWith(SARAPISUI_);
    }

    public static String getOfficialSource(String source) {
        return !isSourceOfficial(source) ? source.substring(SARAPISUI_.length()) : source;
    }

    public static boolean areSourcesEquals(String source1, String source2) {
        return areSourcesEquals(source1, source2, true);
    }

    public static boolean areSourcesEquals(String source1, String source2, boolean strict) {
        return !strict
            ? Objects.equals(getOfficialSource(source1), getOfficialSource(source2))
            : Objects.equals(source1, source2);
    }

}
