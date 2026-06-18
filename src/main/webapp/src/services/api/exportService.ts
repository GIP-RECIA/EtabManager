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

import type { ExportResponse, ExportResponseAPI } from '@/types/index.ts'
import axios from 'axios'
import { instance as axiosInstance, errorHandler } from '@/utils/index.ts'

const managedErrors: number[] = [429, 502]

async function getExportResponse(uai: number): Promise<ExportResponse> {
  try {
    const res = await axiosInstance.get<ExportResponseAPI>(
      `/api/export/esidoc/${uai}`,
    )

    return {
      data: res.data,
      httpCode: res.status,
    }
  }
  catch (error: any) {
    if (axios.isAxiosError(error)) {
      const response: ExportResponse = {
        data: error.response?.data,
        httpCode: error.response?.status ?? 0,
      }

      if (!managedErrors.includes(error.response?.status ?? 0)) {
        errorHandler(error, true)
      }

      return response
    }

    return {
      data: undefined,
      httpCode: 0,
    }
  }
}

export {
  getExportResponse,
}
