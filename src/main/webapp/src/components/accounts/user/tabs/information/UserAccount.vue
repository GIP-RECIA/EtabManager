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
import type { User } from '@/types/index.ts'
import { faInfoCircle } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

defineProps<{
  user?: User
}>()

const { t } = useI18n()
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>
        {{ t('page.user.info.account.header') }}
      </h3>
    </header>

    <div class="body">
      <ul>
        <li v-if="user?.uid">
          <h4>
            {{ t('page.user.info.account.uid') }}
          </h4>
          <SafeEmptyData
            :value="user.uid"
          />
        </li>
        <li>
          <h4>
            {{ t('page.user.info.account.login') }}
          </h4>
          <div class="user-login">
            <SafeEmptyData
              :value="
                user?.guichet
                  ? t('page.user.office.external')
                  : user?.login
              "
            />
            <span
              v-if="user?.guichet"
              :title="t(`page.user.office.${user.guichet}`)"
            >
              <FontAwesomeIcon
                :icon="faInfoCircle"
                size="lg"
              />
            </span>
          </div>
        </li>
        <li v-if="user?.password">
          <h4>
            {{ t('page.user.info.account.password') }}
          </h4>
          <SafeEmptyData
            :value="user.password"
          />
        </li>
        <li>
          <h4>
            {{ t('page.user.info.account.email') }}
          </h4>
          <SafeEmptyData
            :value="user?.email"
          />
        </li>
        <li class="full-width">
          <h4>
            {{ t('page.user.info.account.pronote.header') }}
          </h4>
          <div class="user-pronote">
            <SafeEmptyData
              :value="user?.idPronote"
            />
            <span
              v-if="!user?.idPronote"
              :title="t('page.user.info.account.pronote.info')"
            >
              <FontAwesomeIcon
                :icon="faInfoCircle"
                size="lg"
              />
            </span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.info-card {
  > .body {
    > ul {
      @include unstyled-list;
      display: flex;
      flex-direction: column;
      gap: 16px;

      > li {
        > h4 {
          margin-bottom: 4px;
        }

        > .user-login,
        > .user-pronote {
          display: inline-flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, xl)) {
    > .body > ul {
      display: grid;
      grid-template-columns: repeat(2, 1fr);

      > .full-width {
        grid-column: span 2;
      }
    }
  }
}
</style>
