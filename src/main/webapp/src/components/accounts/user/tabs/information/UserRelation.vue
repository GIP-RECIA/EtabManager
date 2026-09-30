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
import type { IconDefinition } from '@fortawesome/free-solid-svg-icons'
import type { enumValues, UserRelation } from '@/types/index.ts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import { categorieRelationMap, etatMap } from '@/types/enums/index.ts'
import { getIconDefinition, getStateLabel } from '@/utils/index.ts'

const props = defineProps<{
  relation: UserRelation
}>()

const { t } = useI18n()

const etat = computed<{ icon: IconDefinition } & enumValues>(() => ({
  icon: getIconDefinition(props.relation.personneEnRelation.local),
  ...etatMap[props.relation.personneEnRelation.etat],
}))

const suppressDate = computed<string | undefined>(() => (
  props.relation.personneEnRelation?.dateSuppression
    ? format(props.relation.personneEnRelation.dateSuppression, 'P')
    : undefined
))

const title = computed<string>(() => (
  getStateLabel(
    etat.value.i18n,
    suppressDate.value,
    t,
  )))
</script>

<template>
  <div class="user-relation">
    <span
      :title="title"
    >
      <FontAwesomeIcon
        :icon="etat.icon"
        size="lg"
        :style="{
          color: etat.color,
        }"
      />
    </span>
    <div>
      <RouterLink
        :to="{
          name: 'user',
          params: { userId: relation.personneEnRelation.id } }"
      >
        {{ relation.personneEnRelation.cn }}
        <span aria-hidden="true" />
      </RouterLink>

      <p class="description">
        {{
          t(
            categorieRelationMap[relation.categorieRelation].i18n,
            +relation.holder,
          )
        }}
      </p>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.user-relation {
  display: flex;
  gap: 8px;
  height: 100%;
  padding: 16px;
  position: relative;
  border-radius: 10px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
  outline-color: transparent;
  outline-offset: -1px;
  transition:
    outline 0.15s ease-out,
    box-shadow 0.15s ease-out;

  &:has(> div > a) {
    &:hover,
    &:has(:focus-visible) {
      outline: 2px solid var(--#{$prefix}primary);
    }
  }

  > span {
    z-index: 2;
  }

  > div {
    > a {
      @include unstyled-link;
      flex: 1 1 auto;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;

      &:focus-visible {
        outline: none;
      }

      > span {
        position: absolute;
        z-index: 1;
        inset: 0;
      }
    }

    > .description {
      opacity: 0.6;
    }
  }
}
</style>
