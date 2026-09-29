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
import type { SearchUser } from '@/types/index.ts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { computed, h, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useSearchUserQuery } from '@/services/queries/index.ts'
import { etatMap } from '@/types/enums/index.ts'
import {
  concatenate,
  getIconDefinition,
  getStateLabel,
  normalize,
} from '@/utils/index.ts'

const props = defineProps<{
  structureId?: number
}>()

const modelValue = defineModel<SearchUser | undefined>()

const { t } = useI18n()

const search = ref<string>('')

const shortSearch = computed<string>(() => (
  search.value.length >= 3
    ? normalize(search.value).slice(0, 3)
    : ''
))

const { data, isLoading } = useSearchUserQuery(
  shortSearch,
  {
    staff: true,
    check_rights: false,
    not_in_etab: props.structureId,
  },
)

const items = computed<SearchUser[]>(() => {
  const q = normalize(search.value)

  if (!data.value || q.length < 3)
    return []

  return data.value.filter((user) => {
    let filter = user.cn.toLowerCase().includes(q)
    if (user.email)
      filter = filter || user.email.toLowerCase().includes(q)
    if (user.uid)
      filter = filter || user.uid.toLowerCase().includes(q)

    return filter
  })
})

function renderEtat(user: SearchUser) {
  const etat = {
    icon: getIconDefinition(user.local),
    ...etatMap[user.etat],
  }
  const suppressDate = user.dateSuppression
    ? format(user.dateSuppression, 'P')
    : undefined
  const title = getStateLabel(
    etat.i18n,
    suppressDate,
    t,
  )

  return h(
    'span',
    {
      title,
    },
    [
      h(FontAwesomeIcon, {
        icon: etat.icon,
        size: 'lg',
        style: {
          color: etat.color,
        },
      }),
    ],
  )
}
</script>

<template>
  <v-autocomplete
    v-model="modelValue"
    v-model:search.trim="search"
    :label="t('page.account.dialog.manageAdditional.user')"
    :items="items"
    item-value="id"
    item-title="cn"
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
            [item.raw.email, item.raw.uid],
            ' ',
          )
        "
      >
        <template #title>
          {{ item.raw.cn }}
        </template>
        <template #prepend>
          <component :is="renderEtat(item.raw)" />
        </template>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;
</style>
