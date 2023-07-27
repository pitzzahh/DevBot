package tech.araopj.springpitzzahhbot.services.configs;

import tech.araopj.springpitzzahhbot.configs.CategoryConfig;
import org.springframework.stereotype.Service;

@Service
public record CategoryService(CategoryConfig categoryConfig) {

    public String updatesCategoryName() {
        return categoryConfig.getMemberUpdatesCategory();
    }

    public String secretsCategoryName() {
        return categoryConfig.getCreateConfessionsCategory();
    }
}
