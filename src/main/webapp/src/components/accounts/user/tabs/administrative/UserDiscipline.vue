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
  UserDisciplineWithDate,
  UserDisciplineWithDateAndEndInfo,
} from '@/types/index.ts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const props = withDefaults(
  defineProps<{
    discipline: UserDisciplineWithDateAndEndInfo
    clickable?: boolean
  }>(),
  {
    clickable: false,
  },
)

defineEmits<{
  tagClick: [discipline: UserDisciplineWithDate]
}>()

const { t } = useI18n()

const title = computed<string | undefined>(() => {
  const {
    i18n,
    date,
    months,
  } = props.discipline.endInfo ?? {}

  return i18n && date
    ? t(
        i18n,
        {
          date: format(date, 'P'),
          months,
        },
        (months && months > 0)
          ? months
          : 1,
      )
    : undefined
})
</script>

<template>
  <button
    v-if="clickable"
    type="button"
    class="tag small"
    :style="{
      color: discipline.endInfo?.color,
    }"
    @click="$emit('tagClick', discipline)"
  >
    {{ discipline.libelle }}
    <span
      v-if="discipline.endInfo"
      :title="title"
    >
      <FontAwesomeIcon
        v-if="discipline.endInfo?.icon"
        :icon="discipline.endInfo.icon"
      />
    </span>
  </button>
  <span
    v-else
    class="tag-primary"
    :style="{
      color: discipline.endInfo?.color,
    }"
  >
    {{ discipline.libelle }}
    <span
      v-if="discipline.endInfo"
      :title="title"
    >
      <FontAwesomeIcon
        v-if="discipline.endInfo?.icon"
        :icon="discipline.endInfo.icon"
      />
    </span>
  </span>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;
</style>
