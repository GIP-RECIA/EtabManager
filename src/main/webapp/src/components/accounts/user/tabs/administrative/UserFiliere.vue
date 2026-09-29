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
import type {
  FunctionForm,
  UserDisciplineWithDate,
  UserFunctionExtended,
} from '@/types/index.ts'
import UserDiscipline from './UserDiscipline.vue'

const props = withDefaults(
  defineProps<{
    fonction: UserFunctionExtended
    clickable?: boolean
  }>(),
  {
    clickable: false,
  },
)

const emit = defineEmits<{
  tagClick: [FunctionForm]
}>()

function tagClick(discipline: UserDisciplineWithDate): void {
  emit('tagClick', {
    filiere: props.fonction.id,
    discipline: discipline.id,
    dateDebut: discipline.dateDebut,
    dateFin: discipline.dateFin,
  })
}
</script>

<template>
  <div class="filiere-card">
    <h4>
      {{ fonction.libelle }}
    </h4>

    <ul v-if="fonction.disciplines.length > 0">
      <li
        v-for="discipline in fonction.disciplines"
        :key="`discipline-tag-${discipline.id}`"
      >
        <UserDiscipline
          :discipline="discipline"
          :clickable="clickable"
          @tag-click="tagClick"
        />
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.filiere-card {
  display: flex;
  flex-direction: column;
  border-radius: 6px;
  border: 1px solid var(--#{$prefix}stroke);
  padding: 16px;

  > ul {
    @include unstyled-list;
    display: flex;
    flex-wrap: wrap;
    gap: 7px 8px;
  }
}
</style>
