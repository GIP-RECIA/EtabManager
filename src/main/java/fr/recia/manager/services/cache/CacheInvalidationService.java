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

package fr.recia.manager.services.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationService {

    private final CacheManager cacheManager;

    public CacheInvalidationService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictPersonne(Long personneId) {
        evict("personneFonctions", personneId);
        evict("personne", personneId);
        evict("personneLDAPByUid", personneId);
    }

    public void evictPersonneAndAssociatedStructures(Long personneId, Long structureId) {
        evict("personneFonctions", personneId);
        evict("personne", personneId);
        evict("personnesWithoutFunctions", structureId);
        evict("structureFonctions", structureId);
        evict("personnesByEtablissement", new SimpleKey(structureId, true));
        evict("personnesByEtablissement", new SimpleKey(structureId, false));
        evict("fonctionAlerts", structureId);
        evict("stuctureDBById", structureId);
        evict("personneLDAPByUid", personneId);
    }

    private void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
