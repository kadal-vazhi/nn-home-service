package com.kadalVazhi.nn_home_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables automatic population of @CreatedDate and @LastModifiedDate fields in BaseEntity.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
