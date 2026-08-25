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
import { faPen } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'

withDefaults(
  defineProps<{
    structure?: Structure
    canEdit?: boolean
    disableEdit?: boolean
    edit?: boolean
  }>(),
  {
    canEdit: true,
    disableEdit: false,
    edit: false,
  },
)

const { t } = useI18n()
</script>

<template>
  <div class="r-card logo-card">
    <img
      :src="structure?.logo ?? '/annuaire_images/default_banner_v1.jpg'"
      :alt="t('page.settings.info.logo')"
    >
    <footer
      v-if="edit && structure && canEdit"
    >
      <button
        type="button"
        :aria-label="`${t('button.edit')} - ${t('page.settings.info.logo')}`"
        :disabled="disableEdit"
        class="btn-primary small"
        @click="() => {}"
      >
        {{ t('button.edit') }}
        <FontAwesomeIcon
          :icon="faPen"
        />
      </button>
    </footer>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.logo-card {
  flex: 0 0 auto;
  justify-content: end;
  position: relative;
  overflow: hidden;
  aspect-ratio: 9 / 4;
  width: 100%;
  max-width: 720px;

  > img {
    position: absolute;
    inset: 0px;
    object-fit: cover;
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
  }

  > footer {
    z-index: 0;
  }
}
</style>
