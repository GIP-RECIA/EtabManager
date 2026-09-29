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
import type { enumValues, User } from '@/types/index.ts'
import { faCircle, faInfoCircle } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { categoriePersonneMap, etatMap } from '@/types/enums/index.ts'

const props = defineProps<{
  user?: User
}>()

const { t } = useI18n()

const etat = computed<enumValues | undefined>(() => (
  props.user
    ? etatMap[props.user.etat]
    : undefined
))

const suppressDate = computed<string | undefined>(() => (
  props.user?.dateSuppression
    ? format(props.user.dateSuppression, 'P')
    : undefined
))
</script>

<template>
  <div class="user-info">
    <div class="user-avatar">
      <img
        :src="user?.photo ?? '/images/icones/noPictureUser.svg'"
        :alt="t('page.user.photo')"
      >
    </div>

    <ul>
      <li>
        <h1>
          <SafeEmptyData
            :value="user?.cn"
          />
        </h1>
      </li>
      <li>
        <SafeEmptyData
          :value="
            user?.categorie
              ? t(categoriePersonneMap[user.categorie].i18n)
              : undefined
          "
        />
      </li>
      <li class="user-status">
        <span class="sr-only">
          {{ t('page.user.status.header') }}
        </span>
        <FontAwesomeIcon
          :icon="faCircle"
          :style="{
            color: etat?.color,
          }"
          size="lg"
        />
        <SafeEmptyData
          :value="etat ? t(etat.i18n) : undefined"
        />
        <span
          v-if="suppressDate"
          :title="t('page.user.status.deletingDate', { suppressDate })"
        >
          <FontAwesomeIcon
            :icon="faInfoCircle"
            size="lg"
          />
        </span>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.user-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 16px;

  > .user-avatar {
    flex: 0 0 auto;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
    overflow: hidden;

    > img {
      object-fit: cover;
      height: 100%;
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      text-align: center;
    }
  }

  > ul {
    @include unstyled-list;
    display: flex;
    flex-direction: column;
    gap: 4px;

    > li {
      > h1 {
        margin-bottom: 0;
        line-height: 1em;
      }

      &.user-status {
        display: inline-flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>
