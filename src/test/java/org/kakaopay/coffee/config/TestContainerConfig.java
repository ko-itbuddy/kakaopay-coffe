package org.kakaopay.coffee.config;

import com.redis.testcontainers.RedisContainer;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public class TestContainerConfig implements BeforeAllCallback {

    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(
        DockerImageName.parse("mysql:8.0.26"))
        .withDatabaseName("coffee-test")
        .withUsername("coffee")
        .withPassword("coffee1234#$#$");
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(
        RedisContainer.DEFAULT_IMAGE_NAME.withTag("6"));
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // MySQL 컨테이너 시작
        MYSQL_CONTAINER.start();
        // Redis 컨테이너 시작
        REDIS_CONTAINER.start();
        // Kafka 컨테이너 시작
        KAFKA_CONTAINER.start();
    }

    public static class IntegrationTestInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            Map<String, String> properties = new HashMap<>();

            // 각 서비스의 연결 정보 설정
            setKafkaProperties(properties);
            setDatabaseProperties(properties);
            setRedisProperties(properties);

            // 애플리케이션 컨텍스트에 속성값 적용
            TestPropertyValues.of(properties).applyTo(applicationContext);
        }

        // Kafka 연결 정보 설정
        private void setKafkaProperties(Map<String, String> properties) {
            properties.put("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());
        }

        // 데이터베이스 연결 정보 설정
        private void setDatabaseProperties(Map<String, String> properties) {
            properties.put("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl());
            properties.put("spring.datasource.username", MYSQL_CONTAINER.getUsername());
            properties.put("spring.datasource.password", MYSQL_CONTAINER.getPassword());
        }

        // Redis 연결 정보 설정
        private void setRedisProperties(Map<String, String> properties) {
            properties.put("spring.redis.host", REDIS_CONTAINER.getHost());
            properties.put("spring.redis.port", REDIS_CONTAINER.getFirstMappedPort().toString());
        }
    }
}
