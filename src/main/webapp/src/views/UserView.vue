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
import type {
  FunctionForm,
  UserFunction,
  UserStructure,
} from '@/types/index.ts'
import {
  faEllipsisVertical,
  faKey,
  faLink,
  faLock,
  faLockOpen,
  faRotateLeft,
  faTrashCan,

} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, useTemplateRef, watch, watchEffect } from 'vue'
import { useI18n } from 'vue-i18n'
import ManageAdditionalDialog from '@/components/accounts/dialogs/ManageAdditionalDialog.vue'
import UserAdministrativeTab from '@/components/accounts/user/tabs/UserAdministrativeTab.vue'
import UserGroupsTab from '@/components/accounts/user/tabs/UserGroupsTab.vue'
import UserInformationTab from '@/components/accounts/user/tabs/UserInformationTab.vue'
import UserInfo from '@/components/accounts/user/UserInfo.vue'
import MenuButton from '@/components/MenuButton.vue'
import {
  useNavigationTabs,
  useTabs,
  useUserRights,
} from '@/composables/index.ts'
import {
  useDeleteUserMutation,
  useForceDeleteUserMutation,
  useLockUserMutation,
  useResetUserMutation,
  useUndoDeleteUserMutation,
  useUnlockUserMutation,
  useUserQuery,
} from '@/services/queries/index.ts'

const { t } = useI18n()

const { data: user } = useUserQuery()

const {
  canReset,
  canToggleLock,
  canDelete,
  canUndoDelete,
  canForceDelete,
  canAttach,
  isLocked,
  canSeeClassesAndGroups,
  canSeeFunctions,
  canDoAction,
} = useUserRights(user)

/* Tabs */

const tabs = computed<string[]>(() => [
  'info',
  'groups',
  ...(canSeeClassesAndGroups.value || canSeeFunctions.value
    ? ['administrative']
    : []
  ),
])

const tabsRefs = useTemplateRef<HTMLButtonElement[]>('tab-refs')

const {
  activeTab,
  changeActiveTab,
  setActiveTab,
} = useTabs({
  tabs,
  tabsRefs,
})

const {
  currentTabParams,
  setTabParams,
} = useNavigationTabs()

watch(
  activeTab,
  (val) => {
    setTabParams({
      currentTab: val,
    })
  },
)

watchEffect(() => {
  if (
    currentTabParams.value?.currentTab !== undefined
    && currentTabParams.value.currentTab !== activeTab.value
  ) {
    setActiveTab(currentTabParams.value.currentTab)
  }
})

/* Dialog */

const dialogState = ref<boolean>(false)

const dialogData = ref<{
  title: string
  structureId: number | undefined
  disabledFonctions: UserFunction[] | undefined
  editFonction: FunctionForm | undefined
}>({
  title: '',
  structureId: undefined,
  disabledFonctions: undefined,
  editFonction: undefined,
})

function editFunction(
  struct: UserStructure,
  fun: FunctionForm | undefined,
): void {
  dialogData.value = {
    title: t(
      `page.account.dialog.manageAdditional.title.${fun ? 'edit' : 'add'}`,
      {
        structre: struct.nom,
      },
    ),
    structureId: struct.id,
    disabledFonctions: [
      ...struct.fonctions,
      ...struct.additionalFonctions,
    ],
    editFonction: fun,
  }
  dialogState.value = true
}

/* Actions */

enum Actions {
  RESET,
  TOGGLE_LOCK,
  DELETE,
  FORCE_DELETE,
  UNDO_DELETE,
  ATTACH,
}

const availableActions = computed<{
  uid: string | number
  name: string
  icon?: IconDefinition
  disabled?: boolean
}[]>(() => {
  const actions: {
    uid: string | number
    name: string
    icon?: IconDefinition
    disabled?: boolean
  }[] = []

  if (canReset.value) {
    actions.push({
      uid: Actions.RESET,
      name: t('button.reset'),
      icon: faKey,
    })
  }

  if (canToggleLock.value) {
    actions.push({
      uid: Actions.TOGGLE_LOCK,
      name: t(`button.${isLocked.value ? 'unlock' : 'lock'}`),
      icon: isLocked.value ? faLockOpen : faLock,
    })
  }

  if (canDelete.value) {
    actions.push({
      uid: Actions.DELETE,
      name: t('button.delete'),
      icon: faTrashCan,
    })
  }

  if (canForceDelete.value) {
    actions.push({
      uid: Actions.FORCE_DELETE,
      name: t('button.forceDelete'),
      icon: faTrashCan,
    })
  }

  if (canUndoDelete.value) {
    actions.push({
      uid: Actions.UNDO_DELETE,
      name: t('button.undoDelete'),
      icon: faRotateLeft,
      disabled: !user.value?.etat,
    })
  }

  if (canAttach.value) {
    actions.push({
      uid: Actions.ATTACH,
      name: t('button.attach'),
      icon: faLink,
    })
  }

  return actions
})

const {
  mutate: reset,
} = useResetUserMutation()

const {
  mutate: lock,
} = useLockUserMutation()
const {
  mutate: unlock,
} = useUnlockUserMutation()

const {
  mutate: undoDelete,
} = useUndoDeleteUserMutation()

const {
  mutate: deleteU,
} = useDeleteUserMutation()

const {
  mutate: forceDelete,
} = useForceDeleteUserMutation()

function onAction(uid: string | number): void {
  if (!user.value)
    return

  const { id } = user.value
  switch (uid) {
    case Actions.RESET:
      reset(id)
      break

    case Actions.TOGGLE_LOCK:
      isLocked.value
        ? unlock(id)
        : lock(id)
      break

    case Actions.DELETE:
      deleteU(id)
      break

    case Actions.FORCE_DELETE:
      forceDelete(id)
      break

    case Actions.UNDO_DELETE:
      undoDelete(id)
      break

    case Actions.ATTACH:
      onAttach()
      break

    default:
      break
  }
}

function onAttach(): void {
  dialogData.value = {
    title: t('page.account.dialog.manageAdditional.title.attach'),
    structureId: undefined,
    disabledFonctions: undefined,
    editFonction: undefined,
  }
  dialogState.value = true
}
</script>

<template>
  <div class="container">
    <header>
      <UserInfo
        :user="user"
      />

      <div
        v-if="canDoAction && availableActions.length > 0"
        class="user-actions"
      >
        <MenuButton
          :items="availableActions"
          :btn-title="t('button.action', 2)"
          btn-class="btn-secondary circle small"
          @update:model-value="onAction"
        >
          <FontAwesomeIcon :icon="faEllipsisVertical" />
        </MenuButton>
      </div>
    </header>

    <ul
      role="tablist"
      class="tab-selector"
    >
      <li
        v-for="(tab, index) in tabs"
        :key="index"
      >
        <button
          :id="`user-tab-${index}`"
          ref="tab-refs"
          type="button"
          role="tab"
          :aria-selected="activeTab === index"
          :aria-controls="`user-tabpanel-${index}`"
          :tabindex="activeTab === index ? '0' : '-1'"
          class="tag"
          :class="{
            active: activeTab === index,
          }"
          @click="setActiveTab(index)"
          @keydown="changeActiveTab"
        >
          {{ t(`page.user.tab.${tab}`) }}
        </button>
      </li>
    </ul>

    <UserInformationTab
      v-show="activeTab === 0"
      id="user-tabpanel-0"
      role="tabpanel"
      aria-labelledby="user-tab-0"
      tabindex="0"
      :user="user"
    />

    <UserGroupsTab
      v-show="activeTab === 1"
      id="user-tabpanel-1"
      role="tabpanel"
      aria-labelledby="user-tab-1"
      tabindex="0"
      :user="user"
    />

    <UserAdministrativeTab
      v-show="activeTab === 2"
      id="user-tabpanel-2"
      role="tabpanel"
      aria-labelledby="user-tab-2"
      tabindex="0"
      :user="user"
      @edit-function="editFunction"
    />
  </div>

  <ManageAdditionalDialog
    v-model="dialogState"
    :title="dialogData.title"
    :user="user"
    :structure-id="dialogData.structureId"
    :disabled-fonctions="dialogData.disabledFonctions"
    :edit-fonction="dialogData.editFonction"
  />
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.container {
  display: flex;
  flex-direction: column;
  gap: 16px;

  > header {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > .user-info {
      flex: 1 1 auto;
    }

    > .user-actions {
      flex: 0 0 auto;
      display: flex;
      justify-content: end;
    }
  }

  > ul {
    @include unstyled-list;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  > [role='tabpanel'] {
    display: flex;
    flex-direction: column;
    gap: 32px;

    &:focus-visible {
      outline: 2px dotted var(--#{$prefix}primary);
      outline-offset: 8px;
      border-radius: 10px;
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    > header {
      flex-direction: row;

      > .user-actions {
        display: unset;
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    gap: 32px;

    > [role='tabpanel'] {
      gap: 48px;
    }
  }
}
</style>
