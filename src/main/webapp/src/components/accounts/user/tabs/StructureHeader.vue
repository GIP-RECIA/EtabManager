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
import type { UserStructure } from '@/types'
import { faHouseUser, faLandmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'

defineProps<{
  structure: UserStructure
}>()

const { t } = useI18n()
</script>

<template>
  <div class="heading">
    <h2>
      {{ structure.nom }}
    </h2>
    <p v-show="structure.type || structure.uai || structure.siren">
      {{ structure.type }}
      <span v-show="structure.uai || structure.siren">
        {{ structure.uai ?? structure.siren }}
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

.heading {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;

  > h2 {
    margin-bottom: 0;
  }

  > p:not(.icons) {
    opacity: 0.6;
    font-size: var(--#{$prefix}font-size-sm);
  }

  > p.icons {
    font-size: var(--#{$prefix}font-size-sm);
  }
}
</style>
