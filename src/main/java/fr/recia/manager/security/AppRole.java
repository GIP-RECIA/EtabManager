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

package fr.recia.manager.security;

public enum AppRole {
    READ_GLC,
    WRITE_GLC,
    READ_RENTREE,
    WRITE_RENTREE,
    READ_PARAMETAB,
    WRITE_PARAMETAB,
    READ_GROUP,
    WRITE_GROUP,
    ESIDOC,
    VIEW_UID,
    SEARCH_UID,
    ADMIN_FONCTIONS,
    ATTACH
}
