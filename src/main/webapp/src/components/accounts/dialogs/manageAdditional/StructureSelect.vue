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
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useStructuresQuery } from '@/services/queries/index.ts'
import { concatenate, normalize } from '@/utils/index.ts'

const props = defineProps<{
  exclude?: number[]
}>()

const modelValue = defineModel<SearchStructure | undefined>()

const { t } = useI18n()

const search = ref<string>('')

const { data, isLoading } = useStructuresQuery()

const items = computed<SearchStructure[]>(() => {
  const q = normalize(search.value)

  if (!data.value || q.length < 3)
    return []

  return data.value.filter((structure) => {
    let filter

    if (props.exclude)
      filter = !props.exclude.includes(structure.id)
    filter = filter && structure.nom.toLowerCase().includes(q)
    if (structure.type)
      filter = filter || structure.type.toLowerCase().includes(q)
    if (structure.uai)
      filter = filter || structure.uai.toLowerCase().includes(q)
    if (structure.siren)
      filter = filter || structure.siren.toLowerCase().includes(q)

    return filter
  })
})
</script>

<template>
  <v-autocomplete
    v-model="modelValue"
    v-model:search.trim="search"
    :label="t('page.account.dialog.manageAdditional.structure')"
    :items="items"
    item-value="id"
    item-title="nom"
    :loading="isLoading"
    autocomplete="off"
    variant="solo-filled"
    hide-details
    :hide-no-data="isLoading || search.length < 3"
    return-object
    flat
    :custom-filter="() => true"
    no-filter
  >
    <template #item="{ props: itemProps, item }">
      <v-list-item
        v-bind="itemProps"
        :subtitle="
          concatenate(
            [item.raw.type, item.raw.uai],
            ' ',
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
</style>
