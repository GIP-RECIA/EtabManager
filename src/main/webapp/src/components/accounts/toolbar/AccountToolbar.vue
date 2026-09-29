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
import { faHome } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { onMounted, onUnmounted, useTemplateRef } from 'vue'
import { useNavigationTabs } from '@/composables/index.ts'
import TabItem from './TabItem.vue'
import TabMenu from './TabMenu.vue'

const {
  items,
  isItems,
  removeItem,
} = useNavigationTabs()

/* Gestion du scroll */

const tabList = useTemplateRef('tab-list')

function srollTabList(event: WheelEvent): void {
  if (!tabList.value || event.deltaX !== 0)
    return

  event.preventDefault()
  tabList.value.scrollBy({
    left: event.deltaY,
  })
}

onMounted(() => {
  tabList.value?.addEventListener('wheel', srollTabList)
})

onUnmounted(() => {
  tabList.value?.removeEventListener('wheel', srollTabList)
})
</script>

<template>
  <nav>
    <ul>
      <li id="structures">
        <router-link
          :to="{ name: 'account' }"
          class="btn-tertiary circle"
          title="Retourner à la liste des structures"
        >
          <FontAwesomeIcon
            :icon="faHome"
          />
        </router-link>
      </li>
      <li id="tab-menu">
        <TabMenu
          :items="items"
          @remove-item="removeItem"
        />
      </li>
      <li
        v-show="isItems"
        id="tab-list"
      >
        <div>
          <ul ref="tab-list">
            <li
              v-for="item in items"
              :key="item.id"
            >
              <TabItem
                :id="item.id"
                :name="item.name"
                :to="item.to"
                @close="removeItem"
              />
            </li>
          </ul>
        </div>
      </li>
    </ul>
  </nav>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

@media (width >= map.get($grid-breakpoints, md)) {
  body.navigation-drawer-visible {
    nav {
      margin-left: var(--#{$prefix}drawer-width);
    }
  }
}

nav {
  position: fixed;
  z-index: 1029;
  right: 0;
  left: 0;
  background-color: var(--#{$prefix}body-bg);
  display: flex;
  align-items: center;
  padding: 8px;
  height: $account-header-nav-height;

  > ul {
    @include unstyled-list;
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 0;

    > li {
      flex: 0 0 auto;

      &:nth-child(-n + 2) {
        z-index: 3;
      }

      &#structures > a.router-link-exact-active {
        color: var(--#{$prefix}primary);
      }

      &#tab-list {
        $spacingY: 8px;
        $spacingX: 16px;
        display: none;
        flex: 0 1 auto;
        min-width: 0;
        margin-left: -$spacingX;
        margin-right: -$spacingX;

        > div {
          position: relative;

          &::before,
          &::after {
            content: '';
            position: absolute;
            top: 0;
            bottom: 0;
            width: 2 * $spacingX;
            z-index: 2;
          }

          &::before {
            left: -$spacingX;
            background: linear-gradient(90deg, var(--#{$prefix}body-bg) 60%, rgba(255, 255, 255, 0) 100%);
          }

          &::after {
            right: -$spacingX;
            background: linear-gradient(270deg, var(--#{$prefix}body-bg) 60%, rgba(255, 255, 255, 0) 100%);
          }

          > ul {
            @include unstyled-list;
            display: flex;
            align-items: center;
            gap: 8px;
            overflow-x: auto;
            scrollbar-width: none;
            scroll-behavior: smooth;
            white-space: nowrap;
            padding: $spacingY $spacingX;
            margin: -$spacingY 0;

            &:focus-visible {
              outline: 2px dotted var(--#{$prefix}primary);
              outline-offset: -4px;
              border-radius: 10px;
            }
          }
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    > ul > li {
      &#tab-menu {
        display: none;
      }

      &#tab-list {
        display: unset;
      }
    }
  }
}
</style>
