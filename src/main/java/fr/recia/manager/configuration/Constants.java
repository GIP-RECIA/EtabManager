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

package fr.recia.manager.configuration;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SARAPISUI_ = "SarapisUi_";
    public static final String SANS_OBJET = "SANS OBJET";
    public static final String JSON_ARRAY_DELIMITER = "\", \"";
    public static final String JSON_ARRAY_PREFIX = "[ \"";
    public static final String JSON_ARRAY_SUFFIX = "\" ]";

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SPRING_PROFILE_TEST = "test";

    public static final String GROUPER_SOURCEID_USER = "esco:ldap";
    public static final String GROUPER_SOURCEID_GROUP = "g:gsa";
    public static final String GROUPER_MEDIA_TYPE = "text/x-json";
    public static final String GROUPER_DIRECT_MEMBERSHIP = "immediate";


}
