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

package fr.recia.manager.configuration;

import fr.recia.redis.session.cleanup.model.RedisCleanupConfig;
import fr.recia.redis.session.cleanup.service.RedisSessionCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class RedisSessionCleanupConfiguration {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    @Qualifier("customRedisTemplate")
    private RedisTemplate<String, Object> customRedisTemplate;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("redis-session-clean-glc-");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }


    @Bean
    public RedisCleanupConfig redisCleanupConfig() throws UnknownHostException {
        return new RedisCleanupConfig(appProperties.getRedis().getIndexPrefix(), InetAddress.getLocalHost().getHostName().equals(appProperties.getSession().getCronAllowedHostname()), appProperties.getSession().getCronExpression());
    }

    @Bean
    public RedisSessionCleanupService redisSessionCleanupService(FindByIndexNameSessionRepository<? extends Session> sessionRepository, RedisCleanupConfig config, TaskScheduler taskScheduler) {
        return new RedisSessionCleanupService(customRedisTemplate, sessionRepository, config, taskScheduler );
    }
}
