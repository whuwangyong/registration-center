package cn.whu.rest;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @Author WangYong
 * @Date 2019/11/11
 * @Time 18:41
 */
@EnableApolloConfig
@Configuration
public class Config {
    @Bean
    public RestTemplate initRestTemplate() {
        return new RestTemplate();
    }

}
