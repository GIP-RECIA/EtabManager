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
import type { AppRole } from '@/types/enums/index.ts'
import { useQueryCache } from '@pinia/colada'
import { PiniaColadaDevtools } from '@pinia/colada-devtools'
import { watchOnce } from '@vueuse/core'
import { storeToRefs } from 'pinia'
import { computed, onBeforeMount } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
import AccountToolbar from '@/components/accounts/toolbar/AccountToolbar.vue'
import {
  useKeepSession,
  useNavigationTabs,
} from '@/composables/index.ts'
import { usePrincipalRightsQueryOptions } from '@/services/queries/index.ts'
import { useConfigurationStore } from '@/stores/index.ts'
import { errorHandler } from '@/utils/index.ts'

const { t } = useI18n()

const route = useRoute()

const configurationStore = useConfigurationStore()
const { init } = configurationStore
const {
  configuration,
  isInit,
} = storeToRefs(configurationStore)

init()

async function hasAnyRole(roles: AppRole[]): Promise<boolean> {
  const queryCache = useQueryCache()
  const { data, error } = await queryCache.refresh(
    queryCache.ensure(
      usePrincipalRightsQueryOptions(),
    ),
  )
  if (error)
    throw error

  return roles.some(role => new Set(data ?? []).has(role))
}

watchOnce(isInit, (newValue) => {
  if (!newValue || !configuration.value?.front.extendedUportal)
    return
  const { header, footer } = configuration.value.front.extendedUportal
  if (header) {
    const extendedUportalHeaderScript = document.createElement('script')
    extendedUportalHeaderScript.setAttribute('src', header.componentPath)
    extendedUportalHeaderScript.setAttribute('charset', 'utf-8')
    document.head.appendChild(extendedUportalHeaderScript)
  }
  if (footer) {
    const extendedUportalFooterScript = document.createElement('script')
    extendedUportalFooterScript.setAttribute('src', footer.componentPath)
    extendedUportalFooterScript.setAttribute('charset', 'utf-8')
    document.head.appendChild(extendedUportalFooterScript)
  }
})

onBeforeMount(() => {
  document.title = __APP_NAME__
})

const isAccountSection = computed(() => (
  route.matched.some(r => r.name === 'accountRoot')
))

const appName = __APP_NAME__

const router = useRouter()

const {
  loadStructure,
  loadUser,
} = useNavigationTabs()

router.beforeEach(async (to, from) => {
  const {
    params: {
      structureId,
      userId,
    },
    meta: {
      roles,
    },
  } = to

  if (roles && !await hasAnyRole(roles)) {
    return {
      name: 'index',
    }
  }

  if (structureId || userId) {
    if (
      structureId
      && typeof structureId === 'string'
    ) {
      try {
        await loadStructure(from, Number(structureId))
      }
      catch (e) {
        errorHandler(e, t('toast.initCurrentEtab'))

        return {
          name: 'account',
        }
      }
    }

    if (
      userId
      && typeof userId === 'string'
    ) {
      try {
        await loadUser(from, Number(userId))
      }
      catch (e) {
        errorHandler(e, t('toast.initCurrentPersonne'))

        return {
          name: 'account',
        }
      }
    }
  }

  return true
})

router.afterEach(() => {
  requestAnimationFrame(() => {
    document.querySelector('#content')?.scrollTo({
      top: 0,
      behavior: 'smooth',
    })
  })
})

const {
  sessionState,
} = useKeepSession()
</script>

<template>
  <header>
    <extended-uportal-header
      v-if="isInit"
      :service-name="appName"
      v-bind="configuration!.front.extendedUportal?.header?.props"
    />
    <AccountToolbar
      v-if="sessionState && isAccountSection"
    />
  </header>
  <div id="content">
    <main>
      <router-view
        v-if="sessionState"
      />
      <p
        v-else
        class="no-session"
      >
        {{ t('noSession') }}
      </p>
    </main>
    <footer>
      <extended-uportal-footer
        v-if="isInit"
        v-bind="configuration!.front.extendedUportal?.footer?.props"
      />
    </footer>
  </div>

  <PiniaColadaDevtools />
</template>

<style lang="scss">
extended-uportal-header {
  display: block;
  height: var(--recia-header-height);
}

.no-session {
  white-space: pre;
}
</style>
