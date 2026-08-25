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
import type { ServiceRight, ServiceRights } from '@/types/index.ts'
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import ManageServiceRightsDialog from '@/components/access/dialogs/ManageServiceRightsDialog.vue'
import ServiceRightsCard from '@/components/access/ServiceRightsCard.vue'
import PageLayout from '@/components/PageLayout.vue'
import StructureSearch from '@/components/StructureSearch.vue'
import { useAppRights } from '@/composables/index.ts'
import {
  useEtablissementsQuery,
  usePrincipalRightsForEtabQuery,
  useRightsQuery,
  useUpdateRightMutation,
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

const { data: servicesRights } = useRightsQuery(selectedStructure)

const { data: structureRights } = usePrincipalRightsForEtabQuery(selectedStructure)

const { canWriteGroup } = useAppRights(structureRights)

const { mutate } = useUpdateRightMutation()

/* Dialog */

const dialogState = ref<boolean>(false)

const serviceRight = ref<Pick<ServiceRights, 'service'> & ServiceRight>()

function edit(
  service: string,
  right: ServiceRight,
): void {
  serviceRight.value = {
    service,
    ...right,
  }
  dialogState.value = true
}

async function save(
  service: string,
  serviceRight: ServiceRight,
  toAdd: string[],
  toRemove: string[],
): Promise<void> {
  if (!selectedStructure.value)
    return

  mutate({
    id: selectedStructure.value,
    service,
    role: serviceRight.role,
    membersToAdd: toAdd,
    membersToRemove: toRemove,
  })
}
</script>

<template>
  <div class="container">
    <PageLayout
      :title="t('page.access.h1')"
    >
      <StructureSearch
        v-model="selectedStructure"
        :search-list="etabs"
      />

      <div>
        <h2>
          {{ t('page.access.services.header') }}
        </h2>

        <div class="services-grid">
          <ServiceRightsCard
            v-for="serviceRights in servicesRights"
            :key="serviceRights.service"
            :service-rights="serviceRights"
            :edit="canWriteGroup"
            @edit="edit"
          />
        </div>
      </div>
    </PageLayout>
  </div>

  <ManageServiceRightsDialog
    v-if="canWriteGroup"
    v-model="dialogState"
    :service-right="serviceRight"
    @update:model-value="dialogState = false"
    @save="save"
  />
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

.services-grid {
  display: grid;
  gap: 32px;

  @media (width >= map.get($grid-breakpoints, md)) {
    gap: 48px;
  }
}
</style>
