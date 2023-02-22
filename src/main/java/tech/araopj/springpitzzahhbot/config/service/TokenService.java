package tech.araopj.springpitzzahhbot.config.service;

import tech.araopj.springpitzzahhbot.config.TokenConfig;
import org.springframework.stereotype.Service;

@Service
public record TokenService(TokenConfig tokenConfig) {

    public String getToken() {
        return tokenConfig.getToken();
    }

}
