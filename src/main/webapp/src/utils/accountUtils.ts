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

import type { IconDefinition } from '@fortawesome/free-solid-svg-icons'
import type { endInfo } from '@/types/index.ts'
import { faUser as farUser } from '@fortawesome/free-regular-svg-icons'
import {
  faHourglassEnd,
  faHourglassHalf,
  faHourglassStart,
  faUser,
} from '@fortawesome/free-solid-svg-icons'
import { differenceInMonths, isPast } from 'date-fns'
import { storeToRefs } from 'pinia'
import { useConfigurationStore } from '@/stores/index.ts'

function getIconDefinition(
  isLocal: boolean | undefined,
): IconDefinition {
  return isLocal
    ? farUser
    : faUser
}

function getDateFin(date: string): endInfo | undefined {
  const configurationStore = useConfigurationStore()
  const { configuration } = storeToRefs(configurationStore)

  if (isPast(date)) {
    return {
      date,
      isPast: true,
      i18n: 'page.user.administrative.function.hourglass.end',
      color: '',
      icon: faHourglassEnd,
    }
  }
  const months: number = differenceInMonths(date, new Date())
  if (months > 24) // TODO : use configuration
    return undefined
  if (months < (configuration.value?.front.endFunctionWarning ?? 2)) {
    return {
      date,
      months: months + 1,
      isPast: false,
      i18n: 'page.user.administrative.function.hourglass.half',
      color: 'warning',
      icon: faHourglassHalf,
    }
  }

  return {
    date,
    months,
    isPast: false,
    i18n: 'page.user.administrative.function.hourglass.start',
    color: 'primary',
    icon: faHourglassStart,
  }
}

function getStateLabel(
  i18n: string,
  suppressDate: string | undefined,
  t: (key: string, params?: any) => string,
): string {
  const base = t(i18n)

  if (!suppressDate)
    return base

  return `${base} (${
    t('page.user.status.deletingDate', {
      suppressDate,
    })
  })`
}

export {
  getDateFin,
  getIconDefinition,
  getStateLabel,
}
