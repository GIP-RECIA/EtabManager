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
import type { User } from '@/types/index.ts'
import UserGroups from './groups/UserGroups.vue'
import StructureHeader from './StructureHeader.vue'

defineProps<{
  user?: User
}>()
</script>

<template>
  <div>
    <div
      v-for="structure in user?.listeStructures"
      :key="`user-groups-structure-${structure.id}`"
      class="structure-groups"
    >
      <StructureHeader
        :structure="structure"
      />

      <UserGroups
        :structure="structure"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.structure-groups {
  display: grid;
  gap: 16px;

  > :deep(.r-card) > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > ul {
      @include unstyled-list;
      display: flex;
      flex-wrap: wrap;
      gap: 7px 8px;
    }
  }

  @media (width >= map.get($grid-breakpoints, lg)) {
    grid-template-columns: repeat(2, 1fr);
    align-items: start;

    > .heading {
      grid-column: span 2;
    }
  }
}
</style>
