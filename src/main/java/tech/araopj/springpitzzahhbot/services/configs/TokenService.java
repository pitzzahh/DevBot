package tech.araopj.springpitzzahhbot.services.configs;

import tech.araopj.springpitzzahhbot.configs.TokenConfig;
import org.springframework.stereotype.Service;

@Service
public record TokenService(TokenConfig tokenConfig) {

    public String getToken() {
        return tokenConfig.getToken();
    }

}
