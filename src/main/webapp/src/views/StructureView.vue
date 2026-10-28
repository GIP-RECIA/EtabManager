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
import type { AppRole } from '@/types/enums/index.ts'
import { faEllipsisVertical, faLink, faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, useTemplateRef, watch, watchEffect } from 'vue'
import { useI18n } from 'vue-i18n'
import ManageAdditionalDialog from '@/components/accounts/dialogs/ManageAdditionalDialog.vue'
import StructureInfo from '@/components/accounts/structure/StructureInfo.vue'
import StructureAccountsTab from '@/components/accounts/structure/tabs/StructureAccountsTab.vue'
import StructureCompTab from '@/components/accounts/structure/tabs/StructureCompTab.vue'
import StructureDashboardTab from '@/components/accounts/structure/tabs/StructureDashboardTab.vue'
import MenuButton from '@/components/MenuButton.vue'
import { useAppRights, useNavigationTabs, useTabs } from '@/composables/index.ts'
import { useStructureQuery } from '@/services/queries/index.ts'

const { t } = useI18n()

const { data: structure } = useStructureQuery()

const structureRights = computed<AppRole[] | undefined>(
  () => structure.value?.permissions,
)

const { canWriteGLC, canAttach } = useAppRights(structureRights)

/* Tabs */

const tabs = computed<string[]>(() => [
  'dashboard',
  'comp',
  'accounts',
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

/* Actions */

enum Actions {
  ATTACH,
  CREATE,
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

  if (canAttach.value) {
    actions.push({
      uid: Actions.ATTACH,
      name: t('button.attach'),
      icon: faLink,
    })
  }

  if (canWriteGLC.value) {
    actions.push({
      uid: Actions.CREATE,
      name: t('button.create'),
      icon: faPlus,
    })
  }

  return actions
})

function onAction(uid: string | number): void {
  switch (uid) {
    case Actions.ATTACH:
      dialogState.value = true
      break

    case Actions.CREATE:
      break

    default:
      break
  }
}
</script>

<template>
  <div class="container">
    <header>
      <StructureInfo
        :structure="structure"
      />

      <div
        v-if="canWriteGLC && availableActions.length > 0"
        class="structure-actions"
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
          :id="`structure-tab-${index}`"
          ref="tab-refs"
          type="button"
          role="tab"
          :aria-selected="activeTab === index"
          :aria-controls="`structure-tabpanel-${index}`"
          :tabindex="activeTab === index ? '0' : '-1'"
          class="tag"
          :class="{
            active: activeTab === index,
          }"
          @click="setActiveTab(index)"
          @keydown="changeActiveTab"
        >
          {{ t(`page.structure.tab.${tab}`) }}
        </button>
      </li>
    </ul>

    <StructureDashboardTab
      v-show="activeTab === 0"
      id="structure-tabpanel-0"
      role="tabpanel"
      aria-labelledby="structure-tab-0"
      tabindex="0"
      :structure="structure"
    />

    <StructureCompTab
      v-show="activeTab === 1"
      id="structure-tabpanel-1"
      role="tabpanel"
      aria-labelledby="structure-tab-1"
      tabindex="0"
      :structure="structure"
    />

    <StructureAccountsTab
      v-show="activeTab === 2"
      id="structure-tabpanel-2"
      role="tabpanel"
      aria-labelledby="structure-tab-2"
      tabindex="0"
      :structure="structure"
    />
  </div>

  <ManageAdditionalDialog
    v-model="dialogState"
    :title="t('page.account.dialog.manageAdditional.title.attach')"
    :structure-id="structure?.id"
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

    > .structure-info {
      flex: 1 1 auto;
    }

    > .structure-actions {
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

      > .structure-actions {
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
