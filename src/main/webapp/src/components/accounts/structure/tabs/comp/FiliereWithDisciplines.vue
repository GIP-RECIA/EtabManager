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
import type { Composition } from '@/types/index.ts'
import DisciplineCard from './DisciplineCard.vue'

defineProps<{
  filiere: Composition
}>()
</script>

<template>
  <ul>
    <li
      v-for="discipline in filiere.disciplines"
      :key="`structure-filiere-${filiere.id}-discipline-${discipline.id}`"
    >
      <DisciplineCard
        header
        :label="discipline.libelle"
        :users="discipline.personnes"
      />
    </li>
    <li v-if="filiere.personnesWithoutDiscipline.length > 0">
      <DisciplineCard
        header
        label="SANS DISCIPLINE"
        :users="filiere.personnesWithoutDiscipline"
      />
    </li>
  </ul>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

ul {
  @include unstyled-list;
  display: grid;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    grid-template-columns: repeat(2, 1fr);

    > li {
      &:only-child,
      &:has(> .r-card > .body > ul > li:nth-child(10)) {
        grid-column: span 2;
      }
    }
  }
}
</style>
