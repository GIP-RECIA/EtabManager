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
import type { Structure } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()
</script>

<template>
  <div class="structure-info">
    <div class="structure-logo">
      <img
        :src="structure?.logo ?? '/annuaire_images/default_banner_v1.jpg'"
        :alt="t('page.structure.logo')"
      >
    </div>

    <ul>
      <li>
        <h1>
          <SafeEmptyData
            :value="structure?.nom"
          />
        </h1>
      </li>
      <li v-if="structure?.type || structure?.uai || structure?.siren">
        {{ structure.type }}
        <span v-show="structure.uai || structure.siren">
          {{ structure.uai ?? structure.siren }}
        </span>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.structure-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;

  > .structure-logo {
    flex: 0 0 auto;
    width: auto;
    height: 120px;
    border-radius: 10px;
    box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
    overflow: hidden;
    aspect-ratio: 9 / 4;

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

      &:nth-child(2) {
        opacity: 0.6;
        font-size: var(--#{$prefix}font-size-sm);
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    flex-direction: row;
  }
}
</style>
