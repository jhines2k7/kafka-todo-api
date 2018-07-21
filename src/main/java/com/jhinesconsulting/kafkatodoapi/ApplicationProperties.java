package com.jhinesconsulting.kafkatodoapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ApplicationProperties {
    private String bootstrapServersUrl;
    private String bootstrapServersPort;
    private String schemaRegistryPort;
    private String topic;

    public String getBootstrapServersUrl() {
        return bootstrapServersUrl;
    }

    public void setBootstrapServersUrl(String bootstrapServersUrl) {
        this.bootstrapServersUrl = bootstrapServersUrl;
    }

    public String getBootstrapServersPort() {
        return bootstrapServersPort;
    }

    public void setBootstrapServersPort(String bootstrapServersPort) {
        this.bootstrapServersPort = bootstrapServersPort;
    }

    public String getSchemaRegistryPort() {
        return schemaRegistryPort;
    }

    public void setSchemaRegistryPort(String schemaRegistryPort) {
        this.schemaRegistryPort = schemaRegistryPort;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
