/**
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

import type { Ref } from 'vue'
import { useQuery } from '@pinia/colada'
import {
  getConfiguration,
  getPrincipalRights,
  getPrincipalRightsForEtab,
} from '@/services/api/index.ts'

function useConfigurationQuery() {
  return useQuery({
    key: ['configuration'],
    query: () => getConfiguration(),
    staleTime: Infinity,
  })
}

function usePrincipalRightsQuery() {
  return useQuery({
    key: ['rights'],
    query: () => getPrincipalRights(),
    staleTime: Infinity,
  })
}

function usePrincipalRightsForEtabQuery(id: Ref<number>) {
  return useQuery({
    key: ['right', id.value],
    query: () => getPrincipalRightsForEtab(id.value),
    enabled: !!id.value && id.value !== -1,
    staleTime: 1000 * 60 * 10,
  })
}

export {
  useConfigurationQuery,
  usePrincipalRightsForEtabQuery,
  usePrincipalRightsQuery,
}
