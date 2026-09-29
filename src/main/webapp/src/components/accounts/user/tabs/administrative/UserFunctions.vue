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
  UserFunction,
  UserFunctionExtended,
} from '@/types/index.ts'
import { computed } from 'vue'
import { getDateFin } from '@/utils/index.ts'
import UserFiliere from './UserFiliere.vue'

const props = withDefaults(
  defineProps<{
    fonctions: UserFunction[] | undefined
    clickable?: boolean
  }>(),
  {
    clickable: false,
  },
)

const emit = defineEmits<{
  tagClick: [function: FunctionForm]
}>()

const data = computed<UserFunctionExtended[] | undefined>(() => (
  props.fonctions?.map((fun) => {
    const disciplines = fun.disciplines.map((dis) => {
      const endInfo = dis.dateFin
        ? getDateFin(dis.dateFin)
        : undefined

      return {
        ...dis,
        endInfo,
      }
    })

    return {
      ...fun,
      disciplines,
    }
  })
))

function onTagClick(
  payload: FunctionForm,
): void {
  emit('tagClick', payload)
}
</script>

<template>
  <UserFiliere
    v-for="fonction in data"
    :key="`function-${fonction.id}`"
    :fonction="fonction"
    :clickable="clickable"
    @tag-click="onTagClick"
  />
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;
</style>
