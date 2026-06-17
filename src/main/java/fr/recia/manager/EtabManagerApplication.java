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

package fr.recia.manager;

import fr.recia.manager.annotation.ExcludeFromJacocoGeneratedReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@ExcludeFromJacocoGeneratedReport
@SpringBootApplication
@EnableCaching
public class EtabManagerApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(EtabManagerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);

        Environment env = app.run(args).getEnvironment();
        log.info(
            """
                Access URLs:
                ----------------------------------------------------------
                \tLocal: \t\thttp://127.0.0.1:{}
                \tExternal: \thttp://{}:{}
                \tProfiles: \t{}
                ----------------------------------------------------------""",
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            env.getProperty("spring.profiles.active")
        );
    }

}
