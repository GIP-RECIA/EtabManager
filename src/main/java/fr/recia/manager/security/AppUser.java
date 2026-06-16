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

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class AppUser extends User {

    private Map<AppRole, Set<String>> rightsForEtabs;
    private Set<AppRole> globalRights;

    public AppUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                   Map<AppRole, Set<String>> rightsForEtabs, Set<AppRole> globalRights) {
        super(username, password, authorities);
        this.rightsForEtabs = rightsForEtabs;
        this.globalRights = globalRights;
    }

}
