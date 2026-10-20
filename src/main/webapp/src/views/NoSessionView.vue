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
  faRotateRight,
  faTriangleExclamation,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'

const { t } = useI18n()

function reload() {
  window.location.reload()
}
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

      <div class="error">
        <FontAwesomeIcon
          :icon="faTriangleExclamation"
          class="icon"
        />
        <span class="text">
          {{ t('error') }}
          <span class="large">
            {{ t('noSession') }}
          </span>
        </span>
        <button
          type="button"
          class="btn-secondary small"
          @click="reload"
        >
          {{ t('button.reload') }}
          <FontAwesomeIcon :icon="faRotateRight" />
        </button>
      </div>
    </PageLayout>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.page-layout {
  > header > .heading > .btn-tertiary {
    align-self: flex-start;
    font-size: var(--#{$prefix}font-size-md);
  }

  > .error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 16px 12px;
    background-color: var(--#{$prefix}body-bg);

    > .icon {
      font-size: 38px;
      margin-bottom: 14px;
      color: var(--#{$prefix}basic-black);
      opacity: 0.1;
    }

    > .text {
      display: flex;
      flex-direction: column;
      text-align: center;
      font-size: var(--#{$prefix}font-size-xs);

      > .large {
        font-size: var(--#{$prefix}font-size-sm);
        font-weight: bold;
        letter-spacing: -0.15px;
      }
    }

    > a,
    > button {
      margin-top: 24px;
    }
  }
}
</style>
