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
import type { RightMember, ServiceRight, ServiceRights } from '@/types/index.ts'
import { faFloppyDisk, faRotateLeft, faTrashCan, faXmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, watchEffect } from 'vue'
import { useI18n } from 'vue-i18n'
import { alphaSort } from '@/utils/index.ts'

const props = defineProps<{
  serviceRight?: Pick<ServiceRights, 'service'> & ServiceRight
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'save': [
    service: string,
    serviceRight: ServiceRight,
    toAdd: string[],
    toRemove: string[],
  ]
}>()

const { t } = useI18n()

const modelValue = defineModel<boolean>({ required: true })

const EMPTY_SERVICE_RIGHT: Pick<ServiceRights, 'service'> & ServiceRight = {
  service: '',
  role: '',
  isAdmin: false,
  description: '',
  currentMembers: [],
  possibleGroups: [],
  mandatoryGroups: [],
  allowPeople: false,
}

const serviceRight = computed(() => props.serviceRight ?? EMPTY_SERVICE_RIGHT)

const checkboxes = computed<RightMember[]>(() => [
  ...serviceRight.value.mandatoryGroups,
  ...serviceRight.value.possibleGroups,
].sort((a, b) => alphaSort(a.displayName, b.displayName, 'asc')))

const disabled = computed<string[]>(() =>
  serviceRight.value.mandatoryGroups.map(g => g.id),
)

const checkedGroups = ref<string[]>([])

const currentUsers = ref<string[]>([])

const changes = computed<{
  toAdd: string[]
  toRemove: string[]
}>(() => {
  const existing = props.serviceRight?.currentMembers.map(({ id }) => id)

  const allChecked = [
    ...checkedGroups.value,
    ...currentUsers.value,
  ]

  return {
    toAdd: allChecked.filter(checked => !existing?.includes(checked)),
    toRemove: existing?.filter(checked => !allChecked.includes(checked)) ?? [],
  }
})

const canSave = computed<boolean>(() => {
  const { toAdd, toRemove } = changes.value

  return toAdd.length > 0 || toRemove.length > 0
})

function save() {
  const { toAdd, toRemove } = changes.value
  emit(
    'save',
    serviceRight.value.service,
    serviceRight.value,
    toAdd,
    toRemove,
  )
  close()
}

function close() {
  emit('update:modelValue', false)
}

function removeUser(id: string) {
  currentUsers.value = currentUsers.value.filter(user => user !== id)
}

function addUser(id: string) {
  currentUsers.value.push(id)
}

watchEffect(
  () => {
    if (!modelValue.value || !props.serviceRight)
      return

    checkedGroups.value = [
      ...props.serviceRight.mandatoryGroups,
      ...props.serviceRight.currentMembers.filter(({ user }) => !user),
    ].map(({ id }) => id)

    currentUsers.value = props.serviceRight.currentMembers
      .filter(({ user }) => user)
      .map(({ id }) => id)
  },
)
</script>

<template>
  <v-dialog
    v-model="modelValue"
    scrollable
    :max-width="1024"
  >
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title>
          <h1 class="mb-0">
            {{ serviceRight.description }}
          </h1>
        </v-toolbar-title>
        <template #append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            class="me-1"
            @click="close"
          />
        </template>
      </v-toolbar>

      <v-card-text class="pt-0 py-3 manage-service-rights-dialog-container">
        <div>
          <h2>
            {{ t('page.access.dialog.group', 2) }}
          </h2>

          <fieldset>
            <legend class="sr-only">
              {{ t('page.access.dialog.group', 2) }}
            </legend>
            <ul>
              <li
                v-for="item in checkboxes"
                :key="item.id"
              >
                <input
                  :id="item.id"
                  v-model="checkedGroups"
                  :value="item.id"
                  type="checkbox"
                  :disabled="disabled.includes(item.id)"
                >
                <label :for="item.id">
                  {{ item.displayName }}
                </label>
              </li>
            </ul>
          </fieldset>
        </div>

        <div
          v-if="serviceRight.allowPeople"
          class="people-layout"
        >
          <h2>
            {{ t('page.access.dialog.people', 2) }}
          </h2>

          <div v-dev class="field">
            <div class="field-layout">
              <div class="field-container">
                <div class="middle">
                  <label for="user">
                    {{ t('page.access.dialog.addUser') }}
                  </label>
                  <input
                    id="user"
                    type="text"
                    placeholder=""
                  >
                </div>
              </div>
              <div class="active-indicator" />
            </div>
          </div>

          <ul class="user-container">
            <li
              v-for="user in serviceRight.currentMembers.filter(({ user }) => user)"
              :key="user.id"
            >
              <div
                :style="{
                  'text-decoration': !currentUsers.includes(user.id)
                    ? 'line-through'
                    : undefined,
                }"
              >
                <span>
                  {{ user.displayName }}
                </span>
                <span>
                  {{ user.mail }}
                </span>
              </div>

              <button
                type="button"
                class="btn-secondary small"
                @click="
                  currentUsers.includes(user.id)
                    ? removeUser(user.id)
                    : addUser(user.id)
                "
              >
                {{
                  t(`button.${
                    currentUsers.includes(user.id)
                      ? 'delete'
                      : 'restore'
                  }`)
                }}
                <FontAwesomeIcon
                  :icon="
                    currentUsers.includes(user.id)
                      ? faTrashCan
                      : faRotateLeft
                  "
                />
              </button>
            </li>
          </ul>
        </div>
      </v-card-text>

      <v-card-actions>
        <button
          type="button"
          class="btn-secondary"
          @click="close"
        >
          {{ t('button.cancel') }}
          <FontAwesomeIcon
            :icon="faXmark"
          />
        </button>
        <button
          type="button"
          :disabled="!canSave"
          class="btn-primary"
          @click="save"
        >
          {{ t('button.save') }}
          <FontAwesomeIcon
            :icon="faFloppyDisk"
          />
        </button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.manage-service-rights-dialog-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.people-layout {
  display: flex;
  flex-direction: column;
  gap: 16px;

  > h2 {
    margin-bottom: unset;
  }
}

.user-container {
  display: grid;
  gap: 8px;

  > li {
    display: flex;
    flex-direction: column;
    gap: 8px;

    > div {
      display: flex;
      flex-direction: column;

      > span:first-child {
        font-weight: bold;
      }
    }

    > button {
      align-self: end;
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    > li {
      flex-direction: row;
      justify-content: space-between;
      align-items: center;

      > button {
        align-self: unset;
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    grid-template-columns: repeat(2, 1fr);
  }
}

ul {
  @include unstyled-list;
}

fieldset {
  border: none;
}
</style>
