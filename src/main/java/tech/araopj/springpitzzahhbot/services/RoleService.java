package tech.araopj.springpitzzahhbot.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public record RoleService() {
    public Role getRoleOrElseThrow(@NotNull Guild guild, String errorMessage, String role, boolean ignoreCase) {
        return Objects.requireNonNull(guild, errorMessage)
                .getRolesByName(role, ignoreCase)
                .stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Cannot find %s role", role)));
    }
}
