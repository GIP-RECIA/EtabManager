/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { createRouter, createWebHistory } from 'vue-router'
import { AppRole } from '@/types/enums/index.ts'

declare module 'vue-router' {
  interface RouteMeta {
    roles?: AppRole[]
  }
}

const isDev = import.meta.env.DEV

const devRoutes = [
  {
    path: '/settings',
    name: 'settings',
    component: () => import('@/views/SettingsView.vue'),
    meta: {
      roles: [
        AppRole.READ_PARAMETAB,
        AppRole.WRITE_PARAMETAB,
      ],
    },
  },
]

const prodRoutes: never[] = []

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      component: () => import('@/views/IndexView.vue'),
    },
    {
      path: '/account',
      name: 'accountRoot',
      children: [
        {
          path: '',
          name: 'account',
          component: () => import('@/views/AccountView.vue'),
        },
        {
          path: 'structure/:structureId(\\d+)',
          name: 'structure',
          component: () => import('@/views/StructureView.vue'),
        },
        {
          path: 'user/:userId(\\d+)',
          name: 'user',
          component: () => import('@/views/UserView.vue'),
        },
        {
          path: ':pathName(.*)',
          redirect: () => {
            return { name: 'account' }
          },
        },
      ],
      meta: {
        roles: [
          AppRole.READ_GLC,
          AppRole.WRITE_GLC,
        ],
      },
    },
    {
      path: '/access',
      name: 'access',
      component: () => import('@/views/AccessView.vue'),
      meta: {
        roles: [
          AppRole.READ_GROUP,
          AppRole.WRITE_GROUP,
        ],
      },
    },
    {
      path: '/restriction',
      name: 'restriction',
      component: () => import('@/views/RestrictionView.vue'),
      meta: {
        roles: [
          AppRole.READ_RENTREE,
          AppRole.WRITE_RENTREE,
        ],
      },
    },
    {
      path: '/esidocexport',
      name: 'esidocexport',
      component: () => import('@/views/EsidocExportView.vue'),
      meta: {
        roles: [
          AppRole.ESIDOC,
        ],
      },
    },
    {
      path: '/:pathName(.*)',
      redirect: () => {
        return { name: 'index' }
      },
    },
    ...(
      isDev
        ? devRoutes
        : prodRoutes
    ),
  ],
})

export default router
