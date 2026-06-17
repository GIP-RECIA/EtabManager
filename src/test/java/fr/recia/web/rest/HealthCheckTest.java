/*
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

package fr.recia.web.rest;

import fr.recia.manager.web.rest.HealthCheckController;
import fr.recia.test.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.PostConstruct;

import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class HealthCheckTest {

    private MockMvc mockHealthCheckMvc;

    @PostConstruct
    public void setup() {
        openMocks(this);
        HealthCheckController healthCheckController = new HealthCheckController();

        mockHealthCheckMvc = standaloneSetup(healthCheckController).build();
    }

    @Test
    void testHealthCheck() throws Exception {
        mockHealthCheckMvc.perform(head("/health-check")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk());
    }

}
