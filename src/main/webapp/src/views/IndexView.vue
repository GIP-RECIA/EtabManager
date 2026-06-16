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
import {
  faArrowLeft,
  faClock,
  faCubesStacked,
  faFileExport,
  faHouse,
  faUserGear,
  faUsersGear,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'

const { t } = useI18n()

const isDev = import.meta.env.DEV
</script>

<template>
  <div class="container">
    <PageLayout
      :title="t('page.index.h1')"
    >
      <template #back>
        <a
          href="/portail"
          target="_self"
          class="btn-tertiary circle"
          title="Retour à l'accueil"
        >
          <FontAwesomeIcon
            :icon="faArrowLeft"
          />
        </a>
      </template>

      <div>
        <h2>
          {{ t('page.index.h2') }}
        </h2>

        <ul>
          <li>
            <router-link
              :to="{ name: 'account' }"
              class="btn-tertiary"
            >
              <FontAwesomeIcon
                :icon="faUserGear"
              />
              {{ t('page.index.account') }}
            </router-link>
          </li>
          <li v-if="isDev">
            <router-link
              :to="{ name: 'access' }"
              class="btn-tertiary"
            >
              <FontAwesomeIcon
                :icon="faCubesStacked"
              />
              {{ t('page.index.access') }}
            </router-link>
          </li>
          <li>
            <a
              href="/portail/api/ExternalURLStats?fname=I2Grouper-UI&service=/grouperUi/"
              class="btn-tertiary"
            >
              <FontAwesomeIcon
                :icon="faUsersGear"
              />
              {{ t('page.index.groups') }}
            </a>
          </li>
          <li v-if="isDev">
            <router-link
              :to="{ name: 'restriction' }"
              class="btn-tertiary"
            >
              <FontAwesomeIcon
                :icon="faClock"
              />
              {{ t('page.index.restriction') }}
            </router-link>
          </li>
          <li v-if="isDev">
            <router-link
              :to="{ name: 'settings' }"
              class="btn-tertiary"
            >
              <FontAwesomeIcon
                :icon="faHouse"
              />
              {{ t('page.index.settings') }}
            </router-link>
          </li>
          <li>
            <a
              href="/portail/p/ESCO-ParamEtab"
              class="btn-tertiary"
            >
              <FontAwesomeIcon
                :icon="faHouse"
              />
              {{ t('page.index.settings') }}
            </a>
          </li>
          <li v-if="isDev">
            <router-link
              :to="{ name: 'esidocexports' }"
              class="btn-tertiary"
            >
              <FontAwesomeIcon
                :icon="faFileExport"
              />
              {{ t('page.index.esidocexports') }}
            </router-link>
          </li>
        </ul>
      </div>
    </PageLayout>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.container {
  margin-top: 32px;
  margin-bottom: 40px;

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
  }
}

.page-layout {
  > header > .heading > .btn-tertiary {
    align-self: flex-start;
    font-size: var(--#{$prefix}font-size-md);
  }

  ul {
    @include unstyled-list;
    display: flex;
    flex-direction: column;
    gap: 16px;

    > li {
      > a {
        > svg {
          font-size: var(--#{$prefix}font-size-xl);
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    ul {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
    }
  }
}
</style>
