package music_service.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.validation.constraints.NotNull;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.Header;

@Configuration
public class ElasticsearchConfig {
    String serverUrl = "https://elastic.trangiangkhanh.site";

    @Value("${elasticsearch.host}")
    @NotNull
    private String elasticsearchHost;

    @Value("${elasticsearch.port}")
    @NotNull
    private int elasticsearchPort;

    @Value("${elasticsearch.username}")
    @NotNull
    private String elasticsearchUsername;

    @Value("${elasticsearch.password}")
    @NotNull
    private String elasticsearchPassword;

    @Bean
    public ElasticsearchClient elasticsearchClient(){
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword)); // Replace with your credentials

        RestClient restClient = RestClient.builder(new HttpHost( elasticsearchHost, elasticsearchPort, "https"))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        // Create the HLRC
        RestHighLevelClient hlrc = new RestHighLevelClientBuilder(restClient)
                .setApiCompatibilityMode(true)
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        return esClient;
    }

}
