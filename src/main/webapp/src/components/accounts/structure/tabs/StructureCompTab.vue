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
import type { Structure } from '@/types/index.ts'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import Disclosure from '@/components/Disclosure.vue'
import { etatFilters, etatMap } from '@/types/enums/index.ts'
import DisciplineCard from './comp/DisciplineCard.vue'
import FiliereWithDisciplines from './comp/FiliereWithDisciplines.vue'
import '@gip-recia/ui-webcomponents/dist/r-filters.js'

defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()

/* Filters */

enum Staff {
  Teaching = 'teaching',
  School = 'school',
  Collectivity = 'collectivity',
  Academic = 'academic',
}

const filters = computed(() => [
  {
    id: 'staff',
    name: 'Personnel',
    type: 'checkbox',
    items: [
      {
        key: 'staff-all',
        value: 'Tous les personnels',
      },
      ...Object.values(Staff).map(staff => ({
        key: staff,
        value: t(`page.structure.comp.filter.staff.${staff}`),
      })),
    ],
  },
  {
    id: 'state',
    name: 'État',
    type: 'checkbox',
    items: [
      {
        key: 'state-all',
        value: 'Tous les états',
      },
      ...etatFilters.map(etat => ({
        key: etat,
        value: t(etatMap[etat].i18n),
      })),
    ],
  },
])

function updateFilters(e: CustomEvent): void {
  // eslint-disable-next-line unused-imports/no-unused-vars
  const { activeFilters } = e.detail
}
</script>

<template>
  <div>
    <r-filters
      v-dev
      :data="filters"
      @update-filters="updateFilters"
    />

    <Disclosure
      v-for="filiere in structure?.filieres"
      :key="`structure-filiere-${filiere.id}`"
      class="filiere"
    >
      <template #heading>
        <h2>
          {{ filiere.libelle }}
        </h2>
        <span class="count">
          {{
            filiere.disciplines.length
              + (filiere.personnesWithoutDiscipline.length > 0 ? 1 : 0)
          }}
        </span>
      </template>

      <FiliereWithDisciplines
        :filiere="filiere"
      />
    </Disclosure>

    <Disclosure
      v-if="
        structure?.withoutFunctions
          && structure?.withoutFunctions.length > 0
      "
      id="withoutFunctions"
    >
      <template #heading>
        <h2>
          {{ t('page.structure.comp.withoutFunctions') }}
        </h2>
        <span class="count">
          {{ structure.withoutFunctions.length }}
        </span>
      </template>

      <DisciplineCard
        :users="structure.withoutFunctions"
      />
    </Disclosure>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

:deep(.disclosure) {
  > header > button {
    > h2 {
      margin-bottom: 0;
    }

    > .count {
      opacity: 0.6;
    }
  }

  > .menu {
    margin-top: 4px;
  }

  &:has(> header > button:focus-visible) {
    outline-offset: 8px;
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    &.filiere > .menu > ul > li {
      &:only-child,
      &:has(> .r-card > .body > ul > li:nth-child(10)) {
        > .r-card > .body > ul {
          grid-template-columns: repeat(auto-fill, minmax(216px, 1fr));
        }
      }
    }

    &#withoutFunctions > .menu > .r-card > .body > ul {
      grid-template-columns: repeat(auto-fill, minmax(216px, 1fr));
    }
  }

  @media (width >= map.get($grid-breakpoints, xxl)) {
    &.filiere > .menu > ul > li {
      &:only-child,
      &:has(> .r-card > .body > ul > li:nth-child(10)) {
        > .r-card > .body > ul {
          grid-template-columns: repeat(auto-fill, minmax(264px, 1fr));
        }
      }
    }

    &#withoutFunctions > .menu > .r-card > .body > ul {
      grid-template-columns: repeat(auto-fill, minmax(264px, 1fr));
    }
  }
}
</style>
