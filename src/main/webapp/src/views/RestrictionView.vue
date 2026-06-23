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
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'
import CloseRestrictions from '@/components/restrictions/CloseRestrictions.vue'
import GlobalRestrictions from '@/components/restrictions/GlobalRestrictions.vue'
import OpenRestrictions from '@/components/restrictions/OpenRestrictions.vue'
import StructureSearch from '@/components/StructureSearch.vue'
import {
  useEtablissementsQuery,
  useRestrictionsQuery,
} from '@/services/queries/index.ts'

const { t } = useI18n()

const { data: etabs } = useEtablissementsQuery()

const selectedStructure = ref<number>(
  etabs.value && etabs.value.length > 0
    ? etabs.value[0].id
    : -1,
)

watch(
  etabs,
  (val) => {
    if (!val || val.length === 0)
      return

    selectedStructure.value = val[0].id
  },
)

const { data: restrictions } = useRestrictionsQuery(selectedStructure)

/* Edit state */

const isChildEdit = ref<boolean>(false)

function setChildEditState(state: boolean): void {
  isChildEdit.value = state
}
</script>

<template>
  <div class="container">
    <PageLayout
      :title="t('page.restriction.h1')"
    >
      <StructureSearch
        v-model="selectedStructure"
        :search-list="etabs"
        :disabled="isChildEdit"
      />

      <div>
        <h2>
          {{ t('page.restriction.section.header') }}
        </h2>

        <div class="info-container">
          <GlobalRestrictions
            :structure-id="selectedStructure"
            :restrictions="restrictions"
            :disable-edit="isChildEdit"
            class="full-width"
            @edit="setChildEditState"
          />

          <template v-if="!restrictions || restrictions.enabled">
            <CloseRestrictions
              :restrictions="restrictions"
            />

            <OpenRestrictions
              :structure-id="selectedStructure"
              :restrictions="restrictions"
              :disable-edit="isChildEdit"
              class="full-width"
              @edit="setChildEditState"
            />
          </template>
        </div>
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

.info-container {
  display: flex;
  flex-direction: column;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-items: start;

    > .full-width {
      grid-column: span 2;
    }
  }
}
</style>
