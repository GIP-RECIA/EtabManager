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
import type { ClassRestriction, LevelRestriction } from '@/types/index.ts'
import { faPlus, faTrashCan } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, useId } from 'vue'
import { useI18n } from 'vue-i18n'
import MenuButton from '@/components/MenuButton.vue'
import ClassRestrictions from '@/components/restrictions/ClassRestrictions.vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { formatDateTime } from '@/utils/index.ts'

const props = withDefaults(
  defineProps<{
    levelRestriction: LevelRestriction
    isEdit?: boolean
  }>(),
  {
    isEdit: false,
  },
)

const { t } = useI18n()

const id = `niveau-${useId()}`

const modelValue = defineModel<LevelRestriction>({
  default: {
    niveau: '',
    dateRentreeNiveau: null,
    classes: [],
  },
})

const hasClasses = computed<boolean>(() => {
  const dataSource = props.isEdit
    ? modelValue.value
    : props.levelRestriction

  return dataSource.classes.some(c => c.dateRentreeClasse !== null)
})

function hasDate(classe: ClassRestriction, index: number) {
  const c = props.isEdit
    ? modelValue.value.classes[index]
    : classe

  return c.dateRentreeClasse !== null
}

const addableClasses = computed<{ uid: string, name: string }[]>(() => (
  modelValue.value?.classes
    .filter(classe => classe.dateRentreeClasse === null)
    .map(classe => ({ uid: classe.classe, name: classe.classe }))
    ?? []
))

function addClass(uid: string | number): void {
  const classe = modelValue.value.classes.find(classe => classe.classe === uid)
  if (classe)
    classe.dateRentreeClasse = ''
}

function deleteLevel(): void {
  modelValue.value.classes.forEach(c => c.dateRentreeClasse = null)
  modelValue.value.dateRentreeNiveau = null
}
</script>

<template>
  <div class="level-card">
    <div class="body">
      <div class="item">
        <h5
          class="h4"
          :class="{
            'sr-only': isEdit,
          }"
        >
          {{ levelRestriction.niveau }}
        </h5>
        <div
          v-if="isEdit"
          class="field"
        >
          <div class="field-layout">
            <div class="field-container">
              <div class="middle">
                <label :for="id">
                  {{ levelRestriction.niveau }}
                </label>
                <input
                  :id="id"
                  v-model="modelValue.dateRentreeNiveau"
                  type="datetime-local"
                  placeholder=""
                >
              </div>
            </div>
            <div class="active-indicator" />
          </div>
        </div>
        <SafeEmptyData
          v-else
          :value="formatDateTime(levelRestriction.dateRentreeNiveau)"
        />
      </div>

      <div v-show="hasClasses">
        <h6 class="h4">
          {{ t('page.restriction.section.open.class', 2) }}
        </h6>
        <ul>
          <li
            v-for="(classe, index) in levelRestriction.classes"
            v-show="hasDate(classe, index)"
            :key="`${id}-${classe.classe}`"
          >
            <ClassRestrictions
              v-model="modelValue.classes[index]"
              :class-restriction="classe"
              :is-edit="isEdit"
            />
          </li>
        </ul>
      </div>
    </div>

    <footer
      v-if="isEdit"
    >
      <button
        type="button"
        :aria-label="`${t('button.delete')} - ${levelRestriction.niveau}`"
        class="btn-secondary small"
        @click="deleteLevel"
      >
        {{ t('button.delete') }}
        <FontAwesomeIcon
          :icon="faTrashCan"
        />
      </button>
      <MenuButton
        v-show="isEdit"
        :items="addableClasses"
        :btn-title="`${t('button.add')} (${t('page.restriction.section.open.class', 2)})`"
        :disabled="addableClasses.length === 0"
        btn-class="btn-secondary small"
        @update:model-value="addClass"
      >
        {{ t('button.add') }}
        <FontAwesomeIcon
          :icon="faPlus"
        />
      </MenuButton>
    </footer>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.level-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  border-radius: 6px;
  border: 1px solid var(--#{$prefix}stroke);
  padding: 16px;
  max-width: calc(100vw - 4 * 16px); // Need to be fixed

  > .body {
    display: flex;
    flex: 1 0 auto;
    flex-direction: column;
    gap: 16px;

    > .item {
      > h5 {
        margin-bottom: 4px;
      }
    }

    > div > ul {
      @include unstyled-list;
      display: grid;
      gap: 16px;
    }
  }

  > footer {
    display: flex;
    justify-content: flex-end;
    flex-wrap: wrap;
    gap: 6px;
    margin-top: 16px;
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > .body > div > ul {
      grid-template-columns: repeat(auto-fill, minmax(256px, 1fr));
    }
  }
}
</style>
