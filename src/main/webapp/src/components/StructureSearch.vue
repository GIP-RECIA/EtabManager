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
import type { SearchStructure } from '@/types/index.ts'
import { concatenate } from '@/utils/index.ts'

defineProps<{
  searchList?: SearchStructure[]
  disabled?: boolean
}>()

const modelValue = defineModel<number | undefined>()
</script>

<template>
  <v-autocomplete
    v-model="modelValue"
    :items="searchList"
    item-value="id"
    item-title="nom"
    :filter-keys="['raw.nom', 'raw.type', 'raw.uai']"
    density="compact"
    variant="solo"
    rounded="xl"
    hide-details
    :disabled="disabled"
  >
    <template #item="{ props: itemProps, item }">
      <v-list-item
        v-bind="itemProps"
        :subtitle="
          concatenate(
            [item.raw.type, item.raw.uai],
            ' - ',
          )
        "
      >
        <template #title>
          {{ item.raw.nom }}
        </template>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.v-autocomplete {
  max-width: 600px;
}
</style>
