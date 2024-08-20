package org.kakaopay.coffee.config;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public class TestContainerConfig implements BeforeAllCallback {

    private static final DockerComposeContainer<?> DOCKER_COMPOSE = new DockerComposeContainer<>(
        new File("src/test/resources/docker-compose.yml"))
        .withExposedService("mysql", 3306, Wait.forLogMessage(".*ready for connections.*", 1))
        .withExposedService("redis", 6379,
            Wait.forLogMessage(".*Ready to accept connections.*", 1));

    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // MySQL 컨테이너 시작
        // Redis 컨테이너 시작
        DOCKER_COMPOSE.start();

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

            String rdbmsHost = DOCKER_COMPOSE.getServiceHost("mysql", 3306);
            int rdbmsPort = DOCKER_COMPOSE.getServicePort("mysql", 3306);
            properties.put("spring.datasource.url",
                "jdbc:mysql://" + rdbmsHost + ":" + rdbmsPort + "/coffee-test");
            properties.put("spring.datasource.username", "root");
            properties.put("spring.datasource.password", "1234");
        }

        // Redis 연결 정보 설정
        private void setRedisProperties(Map<String, String> properties) {

            String redisHost = DOCKER_COMPOSE.getServiceHost("redis", 6379);
            Integer redisPort = DOCKER_COMPOSE.getServicePort("redis", 6379);
            properties.put("spring.data.redis.host", redisHost);
            properties.put("spring.data.redis.port", redisPort.toString());
        }
    }
}
