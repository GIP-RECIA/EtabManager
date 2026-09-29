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
import { format } from 'date-fns'
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
        {{ t('page.user.info.identity.header') }}
      </h3>
    </header>

    <div class="body">
      <ul>
        <li>
          <h4>
            {{ t('page.user.info.identity.civility') }}
          </h4>
          <SafeEmptyData
            :value="user?.civilite"
          />
        </li>
        <li>
          <h4>
            {{ t('page.user.info.identity.lastName') }}
          </h4>
          <SafeEmptyData
            :value="user?.patronyme"
          />
        </li>
        <li>
          <h4>
            {{ t('page.user.info.identity.firstName') }}
          </h4>
          <SafeEmptyData
            :value="user?.givenName"
          />
        </li>
        <li>
          <h4>
            {{ t('page.user.info.identity.birthDate') }}
          </h4>
          <SafeEmptyData
            :value="
              user?.dateNaissance
                ? format(user.dateNaissance, 'P')
                : undefined
            "
          />
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

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
