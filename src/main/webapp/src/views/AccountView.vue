<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import type { VueTable } from '@tanstack/vue-table'
import type { SearchStructure } from '@/types/index.ts'
import {
  columnFilteringFeature,
  createFilteredRowModel,
  createPaginatedRowModel,
  globalFilteringFeature,
  rowPaginationFeature,
  tableFeatures,
  useTable,
} from '@tanstack/vue-table'
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import PageLayout from '@/components/PageLayout.vue'
import Pagination from '@/components/Pagination.vue'
import { useStructuresQuery } from '@/services/queries/index.ts'
import { concatenate, normalize } from '@/utils/index.ts'

const { t } = useI18n()

const userSearch = ref<string>()

/* Table */

const { data: structures } = useStructuresQuery()

const features = tableFeatures({
  columnFilteringFeature,
  globalFilteringFeature,
  rowPaginationFeature,
  filteredRowModel: createFilteredRowModel(),
  paginatedRowModel: createPaginatedRowModel(),
})

const globalFilter = ref<string>()
const columns = [
  {
    accessorFn: (row: SearchStructure) => (
      concatenate(
        [
          row.nom,
          row.type,
          row.uai,
          row.siren,
          row.ville,
        ],
        ' ',
      )
    ),
    id: 'search',
  },
  {
    id: 'card',
    cell: ({ row }: { row: any }) => row.original,
    enableGlobalFilter: false,
  },
]

const table = useTable({
  features,
  columns,
  get data() {
    return structures.value ?? []
  },
  state: {
    get globalFilter() {
      return globalFilter.value
    },
  },
  initialState: {
    pagination: {
      pageIndex: 0,
      pageSize: 20,
    },
  },
  globalFilterFn: (row, columnId, value) => {
    const rowValue = row.getValue<string>(columnId)

    return normalize(rowValue).includes(normalize(value))
  },
  onGlobalFilterChange: (val) => {
    globalFilter.value = val as string
  },
})
</script>

<template>
  <div class="container">
    <PageLayout
      :title="t('page.account.h1')"
    >
      <div v-dev class="users">
        <h2>
          {{ t('page.account.user.header') }}
        </h2>

        <div class="field">
          <div class="field-layout">
            <div class="field-container">
              <div class="middle">
                <label for="user-search">
                  {{ t('page.account.user.search') }}
                </label>
                <input
                  id="user-search"
                  v-model.trim="userSearch"
                  type="text"
                  placeholder=""
                >
              </div>
            </div>
            <div class="active-indicator" />
          </div>
        </div>
      </div>

      <div class="structures">
        <h2>
          {{ t('page.account.structure.header') }}
        </h2>

        <div class="field">
          <div class="field-layout">
            <div class="field-container">
              <div class="middle">
                <label for="structure-search">
                  {{ t('page.account.structure.search') }}
                </label>
                <input
                  id="structure-search"
                  v-model.trim="globalFilter"
                  type="text"
                  placeholder=""
                >
              </div>
            </div>
            <div class="active-indicator" />
          </div>
        </div>

        <ul class="info-container">
          <li
            v-for="row in table.getRowModel().rows"
            :key="row.id"
          >
            <RouterLink
              :to="{
                name: 'structure',
                params: { structureId: row.original.id },
              }"
              class="structure-link"
            >
              <span>
                {{ row.original.nom }}
              </span>
              <span
                :style="{
                  visibility: (row.original.type || row.original.uai)
                    ? undefined
                    : 'hidden',
                }"
                class="description"
              >
                {{ row.original.type }}
                <span v-if="row.original.uai">
                  {{ row.original.uai }}
                </span>
              </span>
            </RouterLink>
          </li>
        </ul>

        <Pagination
          :table="table as VueTable<any, any>"
        />
      </div>
    </PageLayout>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.container {
  margin-top: 32px;
  margin-bottom: 40px;

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
  }
}

.users,
.structures {
  display: flex;
  flex-direction: column;
  gap: 16px;

  > h2 {
    margin-bottom: 0;
  }
}

.structures {
  > ul {
    @include unstyled-list;
    align-items: unset;
  }
}

.info-container {
  display: grid;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-items: start;
  }
}

.structure-link {
  @include unstyled-link;
  display: flex;
  flex-direction: column;
  padding: 16px;
  border-radius: 10px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
  outline-color: transparent;
  outline-offset: -1px;
  transition:
    outline 0.15s ease-out,
    box-shadow 0.15s ease-out;

  > .description {
    opacity: 0.6;
    min-height: 1.5em;
  }

  &:hover,
  &:focus-visible {
    outline: 2px solid var(--#{$prefix}primary);
    box-shadow: var(--#{$prefix}shadow-hover) HEXToRGBA(var(--#{$prefix}primary), 0.2);
  }
}
</style>
