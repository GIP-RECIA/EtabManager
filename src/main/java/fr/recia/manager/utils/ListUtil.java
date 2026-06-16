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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListUtil {

    private ListUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> String toStringList(List<T> list) {
        return toStringList(list, ", ");
    }

    public static <T> String toStringList(List<T> list, String delimiter) {
        return toStringList(list, delimiter, "[ ", " ]");
    }

    public static <T> String toStringList(List<T> list, String delimiter, String prefix, String suffix) {
        return list.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(delimiter, prefix, suffix));
    }

    public static <T> String toStringList(Set<T> list) {
        return toStringList(list, ", ");
    }

    public static <T> String toStringList(Set<T> list, String delimiter) {
        return toStringList(list, delimiter, "[ ", " ]");
    }

    public static <T> String toStringList(Set<T> list, String delimiter, String prefix, String suffix) {
        return list.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(delimiter, prefix, suffix));
    }

}
