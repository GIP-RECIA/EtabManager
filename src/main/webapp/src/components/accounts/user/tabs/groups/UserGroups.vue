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
import type { UserStructure } from '@/types/index.ts'
import { computed } from 'vue'
import { alphaSort } from '@/utils/stringUtils.ts'
import GroupTree from './GroupTree.vue'

const props = defineProps<{
  structure: UserStructure
}>()

const groups = computed<Map<string, any>>(() => {
  const root = new Map()
  const {
    nom,
    uai,
    siren,
    groupes,
  } = props.structure
  const keyToRemove = `${nom}_${uai ?? siren}`

  groupes.sort((a, b) => alphaSort(a, b, 'asc')).forEach((group) => {
    let current = root
    const split = group.split(':')
    const filteredSplit = split.filter(key => key !== keyToRemove)

    filteredSplit.forEach((key, index) => {
      const isLast = index === filteredSplit.length - 1
      if (!current.has(key))
        current.set(key, isLast ? group : new Map())

      current = current.get(key)
    })
  })

  return root
})
</script>

<template>
  <div class="r-card">
    <GroupTree
      class="tree"
      :groups="groups"
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;
</style>
