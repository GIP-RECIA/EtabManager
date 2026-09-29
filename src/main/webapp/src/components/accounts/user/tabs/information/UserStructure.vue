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
import { faHouseUser, faLandmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'

defineProps<{
  structure: UserStructure
}>()

const { t } = useI18n()
</script>

<template>
  <div class="structure-card">
    <RouterLink
      v-if="structure.authorizedForPrincipal"
      :to="{
        name: 'structure',
        params: { structureId: structure.id },
      }"
    >
      <span>
        {{ structure.nom }}
      </span>
      <span
        :style="{
          visibility: (structure.type || structure.uai || structure.siren)
            ? undefined
            : 'hidden',
        }"
        class="description"
      >
        {{ structure.type }}
        <span v-if="structure.uai || structure.siren">
          {{ structure.uai ?? structure.siren }}
        </span>
        <span aria-hidden="true" />
      </span>
    </RouterLink>
    <p v-else>
      <span>
        {{ structure.nom }}
      </span>
      <span
        :style="{
          visibility: (structure.type || structure.uai)
            ? undefined
            : 'hidden',
        }"
        class="description"
      >
        {{ structure.type }}
        <span v-if="structure.uai">
          {{ structure.uai }}
        </span>
      </span>
    </p>

    <p
      v-if="
        structure.structureRattachement
          || structure.structureCourante
      "
      class="icons"
    >
      <span
        v-show="structure.structureRattachement"
        :title="t('page.user.structure.admin')"
      >
        <FontAwesomeIcon
          :icon="faLandmark"
          size="lg"
        />
      </span>
      <span
        v-show="structure.structureCourante"
        :title="t('page.user.structure.current')"
        class="current"
      >
        <FontAwesomeIcon
          :icon="faHouseUser"
          size="lg"
        />
      </span>
    </p>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.structure-card {
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

  &:has(> a) {
    &:hover,
    &:has(:focus-visible) {
      outline: 2px solid var(--#{$prefix}primary);
      box-shadow: var(--#{$prefix}shadow-hover) HEXToRGBA(var(--#{$prefix}primary), 0.2);
    }
  }

  > a,
  > p {
    @include unstyled-link;
    flex: 1 1 auto;
    display: flex;
    flex-direction: column;

    > .description {
      opacity: 0.6;
    }

    > span[aria-hidden='true'] {
      position: absolute;
      z-index: 1;
      inset: 0;
    }

    &:focus-visible {
      outline: none;
    }
  }

  > .icons {
    flex: 0 0 auto;
    display: grid;
    grid-template-rows: repeat(2, 1fr);
    align-items: center;
    gap: 4px;
    margin-top: -2px;
    margin-bottom: -2px;

    > * {
      z-index: 1;
    }

    > .current {
      grid-row: 2;
    }
  }
}
</style>
