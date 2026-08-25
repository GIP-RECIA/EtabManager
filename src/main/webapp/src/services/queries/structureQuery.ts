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
import { defineQueryOptions, useQuery } from '@pinia/colada'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  getEtablissements,
  getPossibleFunctions,
  getStructure,
  getStructures,
} from '@/services/api/index.ts'

function useStructuresQuery() {
  return useQuery({
    key: ['structures'],
    query: () => getStructures(),
    staleTime: Infinity,
  })
}

function useEtablissementsQuery() {
  return useQuery({
    key: ['etablissements'],
    query: () => getEtablissements(),
    staleTime: Infinity,
  })
}

function useEtablissementQuery(id: Ref<number>) {
  return useQuery(() => ({
    key: ['etablissement', id.value],
    query: () => getStructure(id.value),
    enabled: !!id.value && id.value !== -1,
    staleTime: 1000 * 60 * 10,
  }))
}

function useStructureQuery() {
  const route = useRoute()
  const structureId = computed(() => Number(route.params.structureId))

  return useQuery(useStructureQueryOptions(structureId.value))
}

function useStructureQueryOptions(structureId: number) {
  return defineQueryOptions({
    key: ['structure', structureId],
    query: () => getStructure(structureId),
    enabled: !Number.isNaN(structureId),
    staleTime: 1000 * 60 * 60,
  })
}

function usePossibleFunctionsQuery(structureId: Ref<number>) {
  return useQuery(() => ({
    key: ['possible-functions', structureId.value],
    query: () => getPossibleFunctions(structureId.value),
    enabled: !Number.isNaN(structureId.value),
    staleTime: 1000 * 60 * 60,
  }))
}

export {
  useEtablissementQuery,
  useEtablissementsQuery,
  usePossibleFunctionsQuery,
  useStructureQuery,
  useStructureQueryOptions,
  useStructuresQuery,
}
