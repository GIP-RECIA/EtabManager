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
import type { LevelRestriction, StructureRestriction } from '@/types/index.ts'
import {
  faFloppyDisk,
  faPen,
  faPlus,
  faTrashCan,
  faXmark,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { cloneDeep } from 'lodash-es'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import MenuButton from '@/components/MenuButton.vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { useSaveRestrictionsMutation } from '@/services/queries/index.ts'
import { formatDateTime, toDateTime } from '@/utils/index.ts'
import LevelRestrictions from './LevelRestrictions.vue'

const props = withDefaults(
  defineProps<{
    structureId?: number
    restrictions?: StructureRestriction
    canEdit?: boolean
    disableEdit?: boolean
  }>(),
  {
    canEdit: true,
    disableEdit: false,
  },
)

const emit = defineEmits<{
  edit: [boolean]
}>()

const { t } = useI18n()

const { mutate } = useSaveRestrictionsMutation()

const isEdit = ref<boolean>(false)

const canSave = computed<boolean>(() => (
  true
))

const EMPTY_RESTRICTION: StructureRestriction = {
  enabled: true,
  dateDebutBloquage: null,
  dateRentreeDefaut: null,
  dateRentreeEtab: null,
  niveaux: [],
}

const fields = ref<StructureRestriction>(EMPTY_RESTRICTION)
let initalFields: StructureRestriction = EMPTY_RESTRICTION

watch(
  () => props.restrictions,
  (val) => {
    if (!val)
      return

    const niveaux = val.niveaux.map((level) => {
      const classes = level.classes.map((classe) => {
        return {
          ...classe,
          dateRentreeClasse: toDateTime(classe.dateRentreeClasse),
        }
      })

      return {
        ...level,
        dateRentreeNiveau: toDateTime(level.dateRentreeNiveau),
        classes,
      }
    })

    initalFields = {
      ...val,
      dateRentreeEtab: toDateTime(
        val.dateRentreeEtab !== null
          ? val.dateRentreeEtab
          : val.dateRentreeDefaut,
      ),
      niveaux,
    }
    fields.value = cloneDeep(initalFields)
  },
  {
    immediate: true,
    deep: true,
  },
)

const isLevel = computed<boolean>(() => {
  if (!props.restrictions)
    return false

  const levels = isEdit.value
    ? fields.value.niveaux
    : props.restrictions.niveaux

  return levels
    .some(level => (
      level.dateRentreeNiveau !== null
      || level.classes.some(c => c.dateRentreeClasse !== null)
    ))
})

function hasLevel(level: LevelRestriction, index: number): boolean {
  const l = isEdit.value
    ? fields.value.niveaux[index]
    : level

  return (
    l.dateRentreeNiveau !== null
    || l.classes.some(c => c.dateRentreeClasse !== null)
  )
}

function toggleEdit(): void {
  if (isEdit.value)
    fields.value = cloneDeep(initalFields)
  isEdit.value = !isEdit.value
  emit('edit', isEdit.value)
}

function save(): void {
  if (!props.structureId)
    return

  const clearEmpty = (date: string | null): string | null => {
    return date && date.trim().length > 1 ? date : null
  }

  const body = {
    enabled: fields.value.enabled,
    dateDebutBloquage: clearEmpty(fields.value.dateDebutBloquage),
    dateRentreeDefaut: clearEmpty(fields.value.dateRentreeDefaut),
    dateRentreeEtab: clearEmpty(fields.value.dateRentreeEtab),
    niveaux: fields.value.niveaux.map((level) => {
      const classes = level.classes.map((classe) => {
        return {
          ...classe,
          dateRentreeClasse: clearEmpty(classe.dateRentreeClasse),
        }
      })

      return {
        ...level,
        dateRentreeNiveau: clearEmpty(level.dateRentreeNiveau),
        classes,
      }
    }),
  }

  mutate({
    id: props.structureId,
    body,
  })
  toggleEdit()
}

const addableLevels = computed<{ uid: string, name: string }[]>(() => (
  fields.value?.niveaux
    .filter(level => (
      level.dateRentreeNiveau === null
      && level.classes.every(c => c.dateRentreeClasse === null)
    ))
    .map(level => ({ uid: level.niveau, name: level.niveau }))
    ?? []
))

function addLevel(uid: string | number): void {
  const level = fields.value.niveaux.find(level => level.niveau === uid)
  if (level)
    level.dateRentreeNiveau = ''
}

function deleteCustom(): void {
  fields.value.dateRentreeEtab = ''
}
</script>

<template>
  <div class="r-card open-restrictions-card">
    <div class="body">
      <div class="item">
        <h3>
          {{ t('page.restriction.section.open.header') }}
        </h3>
        <div class="item-container">
          <div>
            <p>{{ t('page.restriction.section.open.default') }}</p>
            <SafeEmptyData
              :value="formatDateTime(props.restrictions?.dateRentreeDefaut)"
            />
          </div>
          <div
            v-if="isEdit"
            class="input-container"
          >
            <div class="field">
              <div class="field-layout">
                <div class="field-container">
                  <div class="middle">
                    <label for="dateRentreeEtab">
                      {{ t('page.restriction.section.open.custom') }}
                    </label>
                    <input
                      id="dateRentreeEtab"
                      v-model="fields.dateRentreeEtab"
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
              :aria-label="`${t('button.delete')} - ${t('page.restriction.section.open.custom')}`"
              :title="t('button.delete')"
              class="btn-tertiary circle"
              @click="deleteCustom"
            >
              <FontAwesomeIcon
                :icon="faTrashCan"
              />
            </button>
          </div>
          <div
            v-else-if="
              restrictions
                && restrictions.dateRentreeEtab !== restrictions.dateRentreeDefaut
            "
          >
            <p>{{ t('page.restriction.section.open.custom') }}</p>
            <SafeEmptyData
              :value="formatDateTime(restrictions.dateRentreeEtab)"
            />
          </div>
        </div>
      </div>

      <div
        v-if="restrictions"
        v-show="isLevel"
      >
        <h4>
          {{ t('page.restriction.section.open.level', 2) }}
        </h4>
        <div class="niveau-container">
          <LevelRestrictions
            v-for="(niveau, index) in restrictions.niveaux"
            v-show="hasLevel(niveau, index)"
            :key="`restriction-level-${niveau.niveau}`"
            v-model="fields.niveaux[index]"
            :level-restriction="niveau"
            :is-edit="isEdit"
          />
        </div>
      </div>
    </div>

    <footer
      v-if="restrictions && canEdit"
    >
      <MenuButton
        v-show="isEdit"
        :items="addableLevels"
        :btn-title="`${t('button.add')} (${t('page.restriction.section.open.level', 2)})`"
        :disabled="addableLevels.length === 0"
        btn-class="btn-secondary small"
        @update:model-value="addLevel"
      >
        {{ t('button.add') }}
        <FontAwesomeIcon
          :icon="faPlus"
        />
      </MenuButton>
      <button
        type="button"
        :aria-label="`${t(`button.${isEdit ? 'cancel' : 'edit'}`)} - ${t('page.restriction.section.open.header')}`"
        :disabled="disableEdit && !isEdit"
        class="small"
        :class="[isEdit ? 'btn-secondary' : 'btn-primary']"
        @click="toggleEdit"
      >
        {{ t(`button.${isEdit ? 'cancel' : 'edit'}`) }}
        <FontAwesomeIcon
          :icon="isEdit ? faXmark : faPen"
        />
      </button>
      <button
        v-show="isEdit"
        type="button"
        :aria-label="`${t('button.save')} - ${t('page.restriction.section.open.header')}`"
        :disabled="!canSave"
        class="btn-primary small"
        @click="save"
      >
        {{ t('button.save') }}
        <FontAwesomeIcon
          :icon="faFloppyDisk"
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

.open-restrictions-card {
  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > .item {
      > h3 {
        margin-bottom: 4px;
      }

      > .item-container {
        > div > p {
          font-weight: bold;
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
    }

    > .item > .item-container,
    > div > .niveau-container {
      display: grid;
      gap: 16px;
      align-items: start;
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > .body {
      > .item > .item-container,
      > div > .niveau-container {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
}
</style>
