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
import type { ClassRestriction } from '@/types/index.ts'
import { faTrashCan } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useId } from 'vue'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { formatDateTime } from '@/utils/index.ts'

withDefaults(
  defineProps<{
    classRestriction: ClassRestriction
    isEdit?: boolean
  }>(),
  {
    isEdit: false,
  },
)

const { t } = useI18n()

const id = `niveau-classe-${useId()}`

const modelValue = defineModel<ClassRestriction>({
  default: {
    classe: '',
    dateRentreeClasse: null,
  },
})

function deleteClass(): void {
  modelValue.value.dateRentreeClasse = null
}
</script>

<template>
  <div class="item">
    <span
      class="h4"
      :class="{
        'sr-only': isEdit,
      }"
    >
      {{ classRestriction.classe }}
    </span>
    <div
      v-if="isEdit"
      class="input-container"
    >
      <div class="field">
        <div class="field-layout">
          <div class="field-container">
            <div class="middle">
              <label :for="id">
                {{ classRestriction.classe }}
              </label>
              <input
                :id="id"
                v-model="modelValue.dateRentreeClasse"
                type="datetime-local"
                placeholder=""
              >
            </div>
          </div>
          <div class="active-indicator" />
        </div>
      </div>
      <button
        type="button"
        :aria-label="`${t('button.delete')} - ${classRestriction.classe}`"
        :title="t('button.delete')"
        class="btn-tertiary circle"
        @click="deleteClass"
      >
        <FontAwesomeIcon
          :icon="faTrashCan"
        />
      </button>
    </div>
    <SafeEmptyData
      v-else
      :value="formatDateTime(classRestriction.dateRentreeClasse)"
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.item {
  > .h4 {
    display: block;
    margin-bottom: 4px;
  }

  > .input-container {
    display: flex;
    align-items: center;
    gap: 8px;

    > .field {
      flex: 1 1 auto;
    }
  }
}
</style>
