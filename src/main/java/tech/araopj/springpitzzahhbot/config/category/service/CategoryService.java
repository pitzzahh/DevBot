package tech.araopj.springpitzzahhbot.config.category.service;

import tech.araopj.springpitzzahhbot.config.category.CategoryConfig;
import org.springframework.stereotype.Service;

@Service
public record CategoryService(CategoryConfig categoryConfig) {

    public String welcomeCategoryName() {
        return categoryConfig.getWelcomeCategory();
    }

    public String updatesCategoryName() {
        return categoryConfig.getMemberUpdatesCategory();
    }

    public String secretsCategoryName() {
        return categoryConfig.getCreateConfessionsCategory();
    }
}
