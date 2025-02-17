package com.icthh.xm.ms.dashboard.config;

import com.icthh.xm.commons.permission.constants.RoleConstant;

import com.icthh.xm.ms.dashboard.security.DomainJwtAccessTokenConverter;
import io.github.jhipster.config.JHipsterProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MicroserviceSecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${ribbon.http.client.enabled:true}")
    private Boolean ribbonTemplateEnabled;

    private final JHipsterProperties jHipsterProperties;

    private final DiscoveryClient discoveryClient;

    public MicroserviceSecurityConfiguration(JHipsterProperties jHipsterProperties,
            DiscoveryClient discoveryClient) {

        this.jHipsterProperties = jHipsterProperties;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/profile-info").permitAll()
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/prometheus/**").permitAll()
            .antMatchers("/management/**").hasAuthority(RoleConstant.SUPER_ADMIN)
            .antMatchers("/swagger-resources/configuration/ui").permitAll();
    }

    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(
        @Qualifier("loadBalancedRestTemplate") RestTemplate keyUriRestTemplate)
        throws CertificateException, IOException {

        DomainJwtAccessTokenConverter converter = new DomainJwtAccessTokenConverter();
        converter.setVerifierKey(getKeyFromConfigServer(keyUriRestTemplate));
        return converter;
    }

    @Bean
    public RestTemplate loadBalancedRestTemplate(ObjectProvider<RestTemplateCustomizer> customizerProvider) {
        RestTemplate restTemplate = new RestTemplate();

        if (ribbonTemplateEnabled) {
            log.info("loadBalancedRestTemplate: using Ribbon load balancer");
            customizerProvider.ifAvailable(customizer -> customizer.customize(restTemplate));
        }

        return restTemplate;
    }

    private String getKeyFromConfigServer(RestTemplate keyUriRestTemplate) throws CertificateException, IOException {
        // Load available UAA servers
        discoveryClient.getServices();
        HttpEntity<Void> request = new HttpEntity<Void>(new HttpHeaders());
        String content = keyUriRestTemplate
            .exchange("http://config/api/token_key", HttpMethod.GET, request, String.class).getBody();

        if (StringUtils.isBlank(content)) {
            throw new CertificateException("Received empty certificate from config.");
        }

        try (InputStream fin = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {

            CertificateFactory f = CertificateFactory.getInstance(Constants.CERTIFICATE);
            X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
            PublicKey pk = certificate.getPublicKey();
            return String.format(Constants.PUBLIC_KEY,
                                 new String(Base64.getEncoder().encode(pk.getEncoded()), StandardCharsets.UTF_8));
        }
    }
}
