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
import type { StructureRestriction } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { formatDateTime } from '@/utils/index.ts'

defineProps<{
  restrictions?: StructureRestriction
}>()

const { t } = useI18n()
</script>

<template>
  <div class="r-card close-restrictions-card">
    <div class="body">
      <div class="item">
        <h3>
          {{ t('page.restriction.section.close.header') }}
        </h3>
        <SafeEmptyData
          :value="formatDateTime(restrictions?.dateDebutBloquage)"
        />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.close-restrictions-card {
  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .item {
      > h3 {
        margin-bottom: 4px;
      }
    }
  }
}
</style>
