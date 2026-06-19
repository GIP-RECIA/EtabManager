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
import type { ToastContainerOptions } from 'vue3-toastify'
import type { ExportResponse, SearchStructure } from '@/types'
import { computed, ref, watch } from 'vue'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'
import StructureSearch from '@/components/StructureSearch.vue'
import { getExportResponse } from '@/services/api/exportService'
import { useEtablissementsQuery } from '@/services/queries/index.ts'
import { i18nHandledErrors } from '@/utils'

const { t } = useI18n()

const { data: etabs } = useEtablissementsQuery()

const awaitingResponse = ref<boolean>(false)

const exportResponse = ref<ExportResponse | undefined>(undefined)

const expectedHttpCodes: number[] = [200, 201, 429, 502]

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

/* Edit state */

const isChildEdit = ref<boolean>(false)

// eslint-disable-next-line unused-imports/no-unused-vars
function setChildEditState(state: boolean): void {
  isChildEdit.value = state
}

const options: ToastContainerOptions = {
  autoClose: false,
}

function showToast(
  type: 'error' | 'warning' | 'success',
  message: string,
): void {
  toast[type](message, options)
}

const header = computed((): string => {
  const etab: SearchStructure | undefined = etabs.value?.find(x => x.id === selectedStructure.value)
  if (etab && etab.uai) {
    return t('page.esidocexports.info.header', { etab: uaiToName(etab.uai) })
  }
  return ''
})

const etabsByUai = computed((): Map<string, string> => {
  const map = new Map<string, string>()

  for (const etab of etabs.value ?? []) {
    if (etab.uai) {
      map.set(etab.uai, etab.nom)
    }
  }

  return map
})

function uaiToName(uai: string): string {
  return etabsByUai.value.get(uai) ?? ''
}

async function exportToEsidoc(): Promise<void> {
  awaitingResponse.value = true
  exportResponse.value = await getExportResponse(selectedStructure.value)
  awaitingResponse.value = false

  if (exportResponse.value.data) {
    exportResponse.value.data.exceptionUais.forEach((uai) => {
      showToast('error', t('page.esidocexports.message.export-exception', { etab: uaiToName(uai) }))
    })

    exportResponse.value.data.alreadyExportedUais.forEach((uai) => {
      showToast('warning', t('page.esidocexports.message.export-already-done', { etab: uaiToName(uai) }))
    })

    exportResponse.value.data.successfulUais.forEach((uai) => {
      showToast('success', t('page.esidocexports.message.success', { etab: uaiToName(uai) }))
    })
  }
}
</script>

<template>
  <div class="container">
    <PageLayout
      :title="t('page.esidocexports.h1')"
    >
      <StructureSearch
        v-model="selectedStructure"
        :search-list="etabs"
        :disabled="isChildEdit"
      />

      <div>
        <h2>
          {{ header }}
        </h2>
        <p class="desc">
          {{ t('page.esidocexports.info.desc') }}
        </p>

        <div>
          <button
            class="btn-primary small"
            :disabled="selectedStructure === -1 || awaitingResponse"
            @click="exportToEsidoc"
          >
            Exporter vers esidoc
          </button>
        </div>
        <p v-if="!awaitingResponse && exportResponse && !expectedHttpCodes.includes(exportResponse.httpCode)">
          {{ t(i18nHandledErrors.includes(exportResponse.httpCode)
            ? `toast.error.${exportResponse.httpCode}`
            : 'toast.error.unmanaged') }}
        </p>
      </div>
    </PageLayout>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

button {
  margin-left: auto;
  display: block;
}

.container {
  margin-top: 32px;
  margin-bottom: 40px;

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
  }
}

.desc {
  white-space: pre-line;
}
</style>
