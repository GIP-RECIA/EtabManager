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

package fr.recia.manager.configuration.bean;

import fr.recia.manager.db.enums.CategorieStructure;
import fr.recia.manager.utils.ListUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.naming.directory.SearchControls;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.recia.manager.configuration.Constants.JSON_ARRAY_DELIMITER;
import static fr.recia.manager.configuration.Constants.JSON_ARRAY_PREFIX;
import static fr.recia.manager.configuration.Constants.JSON_ARRAY_SUFFIX;

@Data
@Validated
public class CustomLdapProperties {

    @NotNull
    private ContextSourceProperties contextSource = new ContextSourceProperties();
    @NotNull
    private LdapTemplateProperties ldapTemplate = new LdapTemplateProperties();
    @NotNull
    private BranchProperties userBranch = new BranchProperties();
    @Nullable
    private GroupBranchProperties groupBranch = new GroupBranchProperties();
    @NotNull
    private StructureBranchProperties structureBranch = new StructureBranchProperties();

    @Data
    @Validated
    public static class ContextSourceProperties {
        @NotEmpty
        private String[] urls;
        @NotBlank
        private String base;
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        private boolean anonymousReadOnly = false;
        private boolean nativePooling = false;
    }

    @Data
    public static class LdapTemplateProperties {
        private boolean ignorePartialResultException = false;
        private boolean ignoreNameNotFoundException = false;
        private boolean ignoreSizeLimitExceededException = true;
        private int searchScope = SearchControls.SUBTREE_SCOPE;
        private int timeLimit = 0;
        private int countLimit = 0;
    }

    @Data
    public static class StructureBranchProperties {
        private String structuresCollFilter;
        private String allStructuresBySirenFilter;
        private String base;
    }

    @Data
    @Validated
    public static class BranchProperties {
        @NotBlank
        private String baseDN = "ou=people";
        @NotBlank
        private String idAttribute = "uid";
        @NotBlank
        private String displayNameAttribute = "displayName";
        @NotBlank
        private String mailAttribute = "mail";
        @NotBlank
        private String searchAttribute = "cn";
        @NotBlank
        private String groupAttribute = "isMemberOf";
        @NotNull
        private Set<String> otherDisplayedAttributes = new HashSet<>();
        @NotNull
        private Set<String> otherBackendAttributes = new HashSet<>();
    }

    @Getter
    @Setter
    @Validated
    public static class GroupBranchProperties extends BranchProperties {

        @NotNull
        private Pattern groupMemberKeyPattern;
        private int groupMemberKeyPatternIndex = 0;
        @NotNull
        private Pattern userMemberKeyPattern;
        private int userMemberKeyPatternIndex = 0;
        private Pattern groupDisplayNamePattern;
        private boolean DNContainsDisplayName = false;
        private boolean resolveUserMembers = false;
        private boolean resolveUserMembersByUserAttributes = true;
        private Pattern dontResolveMembersWithGroupPattern;
        @NotNull
        private StructureProperties structureProperties;

        public GroupBranchProperties() {
            this.setBaseDN("ou=groups");
            this.setGroupAttribute("member");
            this.setIdAttribute("cn");
            this.setDisplayNameAttribute("cn");
        }

        @Data
        @Validated
        public static class StructureProperties {

            private Pattern structureFromGroupPattern;
            private String filterGroupsOfStructure;
            private Map<CategorieStructure, Pattern> structureCategoriesPatterns;
            private Pattern uaiPattern;

        }
    }

}
