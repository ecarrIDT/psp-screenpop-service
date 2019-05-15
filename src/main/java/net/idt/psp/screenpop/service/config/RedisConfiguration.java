package net.idt.psp.screenpop.service.config; 
import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


// MOOSE TODO 
// ==> Look at below to see how to start application using redis when its down and recover 
//     connection when it is back 
//     |  https://github.com/lettuce-io/lettuce-core/issues/882
//  ----------------------------------------------------------------
//  @Profile({"dev","staging"})
//  @Bean(name = "connection", destroyMethod = "close")
//  @RefreshScope
//  StatefulRedisConnection<String, Object> connectionDev(RedisClient redisClient)
//  {
//      try
//      {
//          StatefulRedisConnection<String, Object> connection = redisClient.connect(new SerializedObjectCodec());
//          connection.setTimeout(Duration.ofMillis(timeout));
//          return connection;
//      }
//      catch (Throwable ex)
//      {
//          return Mockito.mock(StatefulRedisConnection.class);
//      }
//  }
//  And in scheduller
//  context.getBean(RefreshScope.class).refresh("connection");
//  ----------------------------------------------------------------

// Modeled after https://www.bytepitch.com/blog/redis-integration-spring-boot/

@Configuration
@Slf4j
@EnableRedisRepositories
@ComponentScan ("net.idt.psp.screenpop.service")
public class RedisConfiguration {

    @Value ("${spring.redis.host}")
    private String redisHost;

    @Value ("${spring.redis.port}")
    private int redisPort;

    @Value ("${spring.redis.password}")
    private String password;

    @Value ("${spring.redis.timeout}")
    private Duration connectionTimeout;

    @Value ("${sprint.redis.lettuce.pool.max-wait:-1ms}")
    private Duration lettuceConnectionMaxWait;
 

    @Bean (destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration(redisHost, redisPort);

// MOOSE TODO need to get encryped password       
        redisStandaloneConfig.setPassword(RedisPassword.of(password));

        //log.debug ("host=" + host + ", port=" + port);
        return redisStandaloneConfig; 
    }
  
    @Bean
    public ClientOptions clientOptions() {
        return ClientOptions.builder()
                   .disconnectedBehavior (ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                   .autoReconnect (true)
                   .build();
    }

    @Bean 
    LettuceClientConfiguration lettuceClientConfig (ClientOptions options, ClientResources dcr) {
        return LettucePoolingClientConfiguration.builder()
                                                .clientOptions(options)
                                                .clientResources(dcr)
                                                .commandTimeout (connectionTimeout)
                                                .build();
    }

    @Bean 
    LettucePoolingClientConfiguration lettucePoolConfig (ClientOptions options, ClientResources dcr) {
        return LettucePoolingClientConfiguration.builder()
                                                .poolConfig(new GenericObjectPoolConfig())
                                                .clientOptions(options)
                                                .clientResources(dcr)
                                                .commandTimeout (connectionTimeout)
                                                .build();
    }
    
    @Bean
    public RedisConnectionFactory redisPoolConnectionFactory (RedisStandaloneConfiguration redisStandaloneConfiguration,
                                                              LettucePoolingClientConfiguration lettucePoolConfig )
    {
        // Set MaxWait for getting a lettuce connection from pool; 
        // Default setting (-1) will block the client forever when the pool is exhausted
        lettucePoolConfig.getPoolConfig().setMaxWaitMillis (lettuceConnectionMaxWait.toMillis());
        LettuceConnectionFactory factory =new LettuceConnectionFactory( redisStandaloneConfiguration,
                                                                        lettucePoolConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
                                                         LettuceClientConfiguration lettuceClientConfig ) 
    {
        LettuceConnectionFactory factory = new LettuceConnectionFactory (redisStandaloneConfiguration, 
                                                                         lettuceClientConfig );
        factory.afterPropertiesSet();
        return factory;
     }
  
     @Bean
     @ConditionalOnMissingBean (name="stringRedisTemplae")
     @Primary
     public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisPoolConnectionFactory) {
// MOOSE, how to switch between using pool or non-pool based connection factory???
         StringRedisTemplate template = new StringRedisTemplate(redisPoolConnectionFactory);
         return template;
     }	
} 
