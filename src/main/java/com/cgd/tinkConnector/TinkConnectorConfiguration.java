package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Utils.DynamicProperties;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "120m")
public class TinkConnectorConfiguration {


    @Value("${tink.tls.main.clientCertificate.active:false}")
    private boolean tinkMainClientCertificateActive;

    @Value("${tink.tls.main.clientcertificate.keystore:null}")
    private String tinkMainClientCertificateKeystore;

    @Value("${tink.tls.main.clientcertificate.keystore.password:null}")
    private String tinkMainClientCertificateKeystorePassword;

    @Value("${tink.tls.main.clientcertificate.keystore.keypassword:null}")
    private String tinkMainClientCertificateKeystoreKeyPassword;

    @Value("${tink.tls.main.clientcertificate.keystore.alias:null}")
    private String tinkMainClientCertificateKeystoreAlias;

    @Value("${cgd.connectTimeout:60000}")
    private int cgdConnectTimeout;

    @Value("${cgd.readTimeout:60000}")
    private int cgdReadTimeout;

    @Value("${tink.connectionRequestTimeout:60000}")
    private int tinkConnectionRequestTimeout;

    @Value("${tink.connectTimeout:60000}")
    private int tinkConnectTimeout;

    @Value("${tink.readTimeout:60000}")
    private int tinkReadTimeout;

    @Value("${tink.proxy.SOCKS.host:null}")
    private String tinkProxySOCKSHost;

    @Value("${tink.proxy.SOCKS.port:0}")
    private int tinkProxySOCKSPort;
    // @Value("${cgd.invertTransactionsSignal:true}")
    public static boolean invertTransactionsSignal = true;


    public static DynamicProperties properties;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST)
    public RestTemplate cgdRestTemplate(@Value("${cgd.baseUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {

        return getCgdTemplate(baseUrl, restTemplateBuilder);
    }

    private RestTemplate getCgdTemplate(String baseUrl, RestTemplateBuilder restTemplateBuilder) {

        return restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .setConnectTimeout(Duration.ofMillis(cgdConnectTimeout))
                .setReadTimeout(Duration.ofMillis(cgdReadTimeout))
                .build();
    }

    private RestTemplate getTinkTemplate(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        HttpClientBuilder clientBuilder = newClientBuilder();

        String proxyHost = System.getProperty("https.proxyHost");
        // configuration where an HTTP proxy is used for connecting to remote TINK host...
        if (!StringUtils.isEmpty(proxyHost)) {
            int proxyPort = Integer.parseInt(System.getProperty("https.proxyPort"));
            String proxyUsername = System.getProperty("https.proxyUser");
            String proxyPassword = System.getProperty("https.proxyPassword");

            HttpHost proxy = new HttpHost(proxyHost, proxyPort);

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(proxyHost, proxyPort),
                    new UsernamePasswordCredentials(proxyUsername, proxyPassword));

            clientBuilder.setProxy(proxy).setDefaultCredentialsProvider(credsProvider).disableCookieManagement();
        }

        /*
        HttpClient httpClient = null;
        if (!StringUtils.isEmpty(tinkProxySOCKSHost) && tinkProxySOCKSPort != 0) {
            LOGGER.info("Will use SOCKS proxy {}:{} for accessing TINK REST API", tinkProxySOCKSHost, tinkProxySOCKSPort);

            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(tinkProxySOCKSHost, tinkProxySOCKSPort));
            SSLConnectionSOCKSSocketFactory socksConnectionFactory = new SSLConnectionSOCKSSocketFactory(SSLContexts.createSystemDefault());
            socksConnectionFactory.setProxy(proxy);

            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", socksConnectionFactory)
                    .build();

            BasicHttpClientConnectionManager connMgr = new BasicHttpClientConnectionManager(reg);
            httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        }

        if (httpClient == null) {
            httpClient = clientBuilder.build();
        }
        */
        HttpClient httpClient = clientBuilder.build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectionRequestTimeout(tinkConnectionRequestTimeout);
        factory.setConnectTimeout(tinkConnectTimeout);
        factory.setReadTimeout(tinkReadTimeout);

        RestTemplate ret = new RestTemplate(factory);
/*
        if (useInterceptores) {

            List<ClientHttpRequestInterceptor> interceptors
                    = ret.getInterceptors();
            if (CollectionUtils.isEmpty(interceptors)) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(new TinkRequestsInterceptor());
            ret.setInterceptors(interceptors);

        }
        */

        ret.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
        return ret;
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST)
    public RestTemplate tinkRestTemplate(@Value("${tink.baseUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {

        return getTinkTemplate(baseUrl, restTemplateBuilder);
    }


    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION)
    public RestTemplate cgdRestTemplateBatch(@Value("${cgd.baseUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {

        return getCgdTemplate(baseUrl, restTemplateBuilder);
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION)
    public RestTemplate tinkRestTemplateBatch(@Value("${tink.baseUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {

        return getTinkTemplate(baseUrl, restTemplateBuilder);
    }


    private HttpClientBuilder newClientBuilder() {
        HttpClientBuilder clientBuilder;
        try {
            // configure client certificate for MTLS (Mutual TLS authentication)
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            if (tinkMainClientCertificateActive) {
                sslContextBuilder.loadKeyMaterial(
                        new File(tinkMainClientCertificateKeystore),
                        tinkMainClientCertificateKeystorePassword.toCharArray(),
                        tinkMainClientCertificateKeystoreKeyPassword.toCharArray(),
                        new PrivateKeyStrategy() {
                            @Override
                            public String chooseAlias(Map<String, PrivateKeyDetails> aliases, Socket socket) {
                                return tinkMainClientCertificateKeystoreAlias;
                            }
                        }
                );
            }

            SSLConnectionSocketFactory sslsf;
            if (StringUtils.isEmpty(tinkProxySOCKSHost) || tinkProxySOCKSPort == 0) {
                // normal communication - direct connection to target host
                sslsf = new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
            } else {
                // communication made via a SOCKS proxy
                SSLConnectionSOCKSSocketFactory factory = new SSLConnectionSOCKSSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(tinkProxySOCKSHost, tinkProxySOCKSPort));
                factory.setProxy(proxy);
                sslsf = factory;
            }

            clientBuilder = HttpClients.custom().setSSLSocketFactory(sslsf);
        } catch (Exception e) {
            // LOGGER.error("newClientBuilder(): error detected while configuring client certificate for SSL, aborting...", e);
            throw new RuntimeException(e);
        }

        return clientBuilder;
    }

    public static class SSLConnectionSOCKSSocketFactory extends SSLConnectionSocketFactory {
        private Proxy proxy;

        public SSLConnectionSOCKSSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
            super(sslContext, hostnameVerifier);
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            if (proxy != null) {
                return new Socket(proxy);
            }
            return super.createSocket(context);
        }

        public void setProxy(Proxy proxy) {
            this.proxy = proxy;
        }
    }

    @Bean
    public java.lang.String clientId(@Value("${tink.clientId}") String clientId) {
        return clientId;
    }

    @Bean
    public java.lang.String clientSecret(@Value("${tink.clientSecret}") String clientSecret) {
        return clientSecret;
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime() // Works on Postgres, MySQL, MariaDb, MS SQL, Oracle, DB2, HSQL and H2
                        .build()
        );
    }

    @Bean
    public JavaMailSender getJavaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("mailhost");
        //mailSender.setPort(587);

        //mailSender.setUsername("my.gmail@gmail.com");
        //mailSender.setPassword("password");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.debug", "true");

        return mailSender;
    }
/*
    @Bean
    public java.lang.String batchFileHost(@Value("${cgd.batchJob.sshHost }") String batchFileHost) {
        return batchFileHost;
    }
*/

}
