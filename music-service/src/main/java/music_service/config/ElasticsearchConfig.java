package music_service.config;

import jakarta.validation.constraints.NotNull;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Configuration
@EnableElasticsearchRepositories(basePackages = "music_service.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.hostAndPort}")
    @NotNull
    private String elasticsearchHostAndPort;

    @Value("${elasticsearch.username}")
    @NotNull
    private String elasticsearchUsername;

    @Value("${elasticsearch.password}")
    @NotNull
    private String elasticsearchPassword;


    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchHostAndPort)
                .usingSsl()
                .withBasicAuth(elasticsearchUsername,elasticsearchPassword)
                .build();
    }

    private static SSLContext buildSSLContext() {
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, new TrustAllStrategy()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
