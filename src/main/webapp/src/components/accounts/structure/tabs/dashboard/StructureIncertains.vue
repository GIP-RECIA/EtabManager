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
import type {
  ExpandedState,
  Row,
  SortingState,
  VueTable,
} from '@tanstack/vue-table'
import type { Incertain, RfilterSection, Structure } from '@/types/index.ts'
import {
  faAngleDown,
  faAngleUp,
  faEye,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  columnFilteringFeature,
  columnVisibilityFeature,
  createColumnHelper,
  createFilteredRowModel,
  createPaginatedRowModel,
  createSortedRowModel,
  FlexRender,
  globalFilteringFeature,
  rowExpandingFeature,
  rowPaginationFeature,
  rowSortingFeature,
  tableFeatures,
  useTable,
} from '@tanstack/vue-table'
import { format } from 'date-fns'
import { computed, Fragment, h, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import Pagination from '@/components/Pagination.vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { etatMap } from '@/types/enums/index.ts'
import { getIconDefinition, getStateLabel } from '@/utils/index.ts'

const props = defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()

/* Filters */

const activeFilters = ref<{ id: string, checked: string[] }[]>([])

function isItemChecked(filterId: string, itemKey: string): boolean {
  const filter = activeFilters.value.find(f => f.id === filterId)
  if (!filter)
    return itemKey === `${filterId}-all`

  return filter.checked.includes(itemKey)
}

const filters = computed<RfilterSection[]>(() => [
  {
    id: 'mandatory',
    name: t('page.structure.dashboard.incertains.filter.mandatory.header'),
    type: 'radio',
    items: [
      {
        key: 'mandatory-all',
        value: t('page.structure.dashboard.incertains.filter.mandatory.all'),
        checked: isItemChecked('mandatory', 'mandatory-all'),
      },
      {
        key: 'yes',
        value: t('page.structure.dashboard.incertains.filter.mandatory.yes'),
        checked: isItemChecked('mandatory', 'yes'),
      },
      {
        key: 'no',
        value: t('page.structure.dashboard.incertains.filter.mandatory.no'),
        checked: isItemChecked('mandatory', 'no'),
      },
    ],
  },
])

function updateFilters(e: CustomEvent): void {
  activeFilters.value = e.detail.activeFilters
}

/* Table */

const accounts = ref<Incertain[]>([])

watch(
  () => props.structure?.incertains,
  (val) => {
    accounts.value = val ?? []
  },
  { immediate: true },
)

const filteredAccounts = computed<Incertain[]>(() => {
  let result = accounts.value

  for (const filter of activeFilters.value) {
    const { id, checked } = filter

    if (checked.length === 0 || checked.includes(`${id}-all`))
      continue

    switch (id) {
      case 'mandatory':
        result = result.filter(user => checked.includes(user.incertains.some(x => x.obligatoire) ? 'yes' : 'no'))
        break
    }
  }

  return result
})

const hasUid = computed<boolean>(() =>
  accounts.value.some(row => row.personne.uid != null),
)

const features = tableFeatures({
  columnFilteringFeature,
  columnVisibilityFeature,
  globalFilteringFeature,
  rowExpandingFeature,
  rowPaginationFeature,
  rowSortingFeature,
  filteredRowModel: createFilteredRowModel(),
  paginatedRowModel: createPaginatedRowModel(),
  sortedRowModel: createSortedRowModel(),
})

function renderEtat(row: Row<typeof features, Incertain>) {
  const etat = {
    icon: getIconDefinition(row.original.personne.local),
    ...etatMap[row.original.personne.etat],
  }
  const suppressDate = row.original.personne.dateSuppression
    ? format(row.original.personne.dateSuppression, 'P')
    : undefined
  const title = etat.i18n
    ? getStateLabel(
        etat.i18n,
        suppressDate,
        t,
      )
    : undefined

  return h(
    'span',
    {
      title,
    },
    [
      h(FontAwesomeIcon, {
        icon: etat.icon,
        size: 'lg',
        style: {
          color: etat.color,
        },
      }),
    ],
  )
}

function renderActions(row: Row<typeof features, Incertain>) {
  return h(
    Fragment,
    [
      h(
        RouterLink,
        {
          to: {
            name: 'user',
            params: { userId: row.original.personne.id },
          },
          class: 'btn-secondary small circle',
        },
        () => [
          h(
            'span',
            {
              title: 'Consulter',
            },
            [
              h(FontAwesomeIcon, {
                icon: faEye,
              }),
            ],
          ),
        ],
      ),
      h(
        'button',
        {
          type: 'button',
          ariaExpanded: row.getIsExpanded(),
          ariaControls: `user-menu-${row.original.personne.id}`,
          class: 'btn-secondary small circle',
          onClick: row.getToggleExpandedHandler(),
        },
        [
          h(
            'span',
            {
              title: 'Développer',
            },
            [
              h(FontAwesomeIcon, {
                icon: faAngleDown,
                style: {
                  rotate: row.getIsExpanded() ? '180deg' : undefined,
                },
              }),
            ],
          ),
        ],
      ),
    ],
  )
}

const columnHelper = createColumnHelper<typeof features, Incertain>()
const globalFilter = ref<string>()
const columns = computed(() => [
  columnHelper.accessor('personne.etat', {
    id: 'etat',
    header: t('page.user.status.header'),
    cell: ({ row }) => renderEtat(row),
    enableGlobalFilter: false,
  }),
  columnHelper.accessor('personne.cn', {
    id: 'nom',
    header: t('page.user.info.identity.lastName'),
  }),
  columnHelper.display({
    id: 'actions',
    header: 'Actions',
    cell: ({ row }) => renderActions(row),
    enableGlobalFilter: false,
  }),
])
const sorting = ref<SortingState>([])
const expanded = ref<ExpandedState>({})

const table = useTable({
  features,
  columns,
  data: filteredAccounts,
  state: {
    get globalFilter() {
      return globalFilter.value
    },
    get sorting() {
      return sorting.value
    },
    get expanded() {
      return expanded.value
    },
  },
  getRowCanExpand: () => true,
  initialState: {
    pagination: {
      pageIndex: 0,
      pageSize: 20,
    },
  },
  onGlobalFilterChange: (val) => {
    globalFilter.value = val as string
  },
  enableMultiSort: true,
  maxMultiSortColCount: 2,
  onSortingChange: (updaterOrValue) => {
    sorting.value = typeof updaterOrValue === 'function'
      ? updaterOrValue(sorting.value)
      : updaterOrValue
  },
  onExpandedChange: (updaterOrValue) => {
    expanded.value = typeof updaterOrValue === 'function'
      ? updaterOrValue(expanded.value)
      : updaterOrValue
  },
})
</script>

<template>
  <div class="incertains">
    <div class="title">
      <h2>
        {{ t('page.structure.dashboard.incertains.header') }}
      </h2>
      <p class="count">
        {{ table.getRowCount() }}
      </p>
    </div>

    <div class="">
      <r-filters
        :data="filters"
        @update-filters="updateFilters"
      />

      <div class="field">
        <div class="field-layout">
          <div class="field-container">
            <div class="middle">
              <label for="structure-search">
                {{ t('page.structure.dashboard.incertains.search') }}
              </label>
              <input
                id="structure-search"
                v-model.trim="globalFilter"
                type="text"
                placeholder=""
              >
            </div>
          </div>
          <div class="active-indicator" />
        </div>
      </div>

      <div class="accounts-data">
        <table>
          <thead>
            <tr
              v-for="headerGroup in table.getHeaderGroups()"
              :key="headerGroup.id"
            >
              <th
                v-for="header in headerGroup.headers"
                :key="header.id"
                :colSpan="header.colSpan"
                :class="[
                  header.column.getCanSort()
                    ? 'cursor-pointer select-none'
                    : '',
                  header.column.columnDef.id,
                ]"
                @click="header.column.getToggleSortingHandler()?.($event)"
              >
                <FlexRender
                  v-if="!header.isPlaceholder"
                  :header="header"
                />

                <FontAwesomeIcon
                  v-if="header.column.getIsSorted()"
                  :icon="
                    header.column.getIsSorted() === 'asc'
                      ? faAngleUp
                      : faAngleDown
                  "
                />
              </th>
            </tr>
          </thead>
          <tbody>
            <template
              v-for="row in table.getRowModel().rows"
              :key="row.id"
            >
              <tr class="contentLine">
                <td
                  v-for="cell in row.getVisibleCells()"
                  :key="cell.id"
                  :class="cell.column.columnDef.id"
                >
                  <FlexRender
                    :cell="cell"
                  />
                </td>
              </tr>
              <tr
                v-show="row.getIsExpanded()"
                :id="`user-menu-${row.original.personne.id}`"
                class="expandedLine"
              >
                <td :colspan="row.getVisibleCells().length">
                  <ul>
                    <li
                      v-for="(incertain, index) in row.original.incertains"
                      :key="`${row.original.personne.id}-${index}`"
                    >
                      <p v-if="hasUid">
                        <span class="label">
                          {{ t('page.structure.dashboard.incertains.table.uid') }}
                        </span>
                        <SafeEmptyData
                          :value="row.original.personne.uid"
                        />
                      </p>
                      <p>
                        <span class="label">
                          {{ t('page.structure.dashboard.incertains.table.texte') }}
                        </span>
                        <SafeEmptyData
                          :value="incertain.texte"
                        />
                      </p>
                      <p>
                        <span class="label">
                          {{ t('page.structure.dashboard.incertains.table.attribut') }}
                        </span>
                        <SafeEmptyData
                          :value="incertain.value ? `${incertain.attribut} : ${incertain.value}` : incertain.attribut"
                        />
                      </p>
                    </li>
                  </ul>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <Pagination
        :table="table as VueTable<any, any>"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.incertains {
  .title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: var(--#{$prefix}font-size-base);

    > h2 {
      margin-bottom: 0;
    }

    > .count {
      opacity: 0.6;
    }
  }

  > div {
    display: grid;
    gap: 16px;
  }
}

.accounts-data {
  > table {
    display: grid;
    border-collapse: collapse;
    table-layout: fixed;
    width: 100%;

    > thead > tr > th,
    > tbody > tr > td {
      &.etat {
        padding: 12px;
        text-align: center;
        width: 70px;
      }
    }

    > tbody > tr > td {
      &.etat {
        width: 40px;
      }
    }

    > thead {
      position: sticky;
      top: $account-header-height;
      z-index: 1;
      background-color: var(--#{$prefix}body-bg);

      > tr > th {
        padding: 12px;
        text-align: start;
      }
    }

    > tbody {
      > tr {
        transition: background-color 0.15s ease;

        &.contentLine {
          display: grid;
          grid-template-columns: 40px 1fr auto;
          grid-template-areas:
            'select nom    actions'
            'etat   prenom actions';
          align-items: center;
          white-space: nowrap;
          border-top: 1px solid var(--#{$prefix}stroke);

          > td {
            &:not(.etat, .actions) {
              padding: 12px 16px;
            }

            &.etat {
              grid-area: etat;
              padding-top: 0;
            }

            &.nom {
              grid-area: nom;
              padding-bottom: 0;
            }

            &.prenom {
              grid-area: prenom;
              padding-top: 0;
            }

            &.actions {
              grid-area: actions;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              gap: 8px;
              padding: 8px;

              > :deep(button) > span > svg {
                transition: rotate 0.2s ease-in-out;
              }
            }
          }
        }

        &.expandedLine {
          display: grid;

          > td > ul {
            @include unstyled-list;

            > li {
              display: grid;
              grid-auto-rows: 1fr;

              > p {
                display: flex;
                flex-direction: column;
                padding: 12px 16px;

                > .label {
                  opacity: 0.6;
                }
              }
            }
          }
        }

        > td {
          vertical-align: middle;
        }

        &:hover,
        &:has(:focus-visible),
        &:hover + tr.expandedLine,
        &:has(:focus-visible) + tr.expandedLine,
        &:has(+ tr.expandedLine:hover) {
          background-color: HEXToRGBA(var(--#{$prefix}btn-secondary-hover), 0.4);
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    > table {
      display: table;

      > thead > tr > th,
      > tbody > tr > td {
        &.actions {
          width: 100px;
        }
      }

      > thead > tr > th {
        padding: 12px 16px;
      }

      > tbody > tr {
        &.contentLine {
          display: table-row;

          > td {
            &.etat {
              padding-top: 12px;
              width: 70px;
            }

            &.nom {
              padding-bottom: 12px;
            }

            &.prenom {
              padding-top: 12px;
            }

            &.actions {
              flex-direction: row;
            }
          }
        }

        &.expandedLine {
          display: table-row;

          > td > ul > li {
            grid-template-columns: repeat(2, 1fr);
          }
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > table > tbody > tr.expandedLine > td > div {
      grid-auto-flow: column;
      grid-auto-columns: 1fr;
    }
  }
}
</style>
