/*
 * MIT License
 *
 * Copyright (c) 2022 Peter John Arao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.araopj.devbot.services.configs;

import tech.araopj.devbot.configs.ModerationConfig;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public record ViolationService(ModerationConfig moderationConfig) {

    /**
     * Adds violation to anyone who says a bad words.
     * @param username the username of the user who violated.
     */
    public void addViolation(final String username) {
        moderationConfig.violations().put(username, getViolationCount(username) + 1);
    }

    private Integer getViolationCount(String username) {
        return moderationConfig.violations()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(username))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
    }

    public boolean violatedThreeTimes(String username) {
        var violated = getViolationCount(username) >= 3;
        if (violated) moderationConfig.violations().remove(username);
        return violated;
    }

}
