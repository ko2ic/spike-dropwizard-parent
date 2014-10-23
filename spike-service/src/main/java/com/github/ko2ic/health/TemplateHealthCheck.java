package com.github.ko2ic.health;

import java.util.Optional;

import com.codahale.metrics.health.HealthCheck;
import com.github.ko2ic.core.Template;

public class TemplateHealthCheck extends HealthCheck {
    private final Template template;

    public TemplateHealthCheck(Template template) {
        this.template = template;
    }

    @Override
    protected Result check() throws Exception {
        return Result.unhealthy(template.render(Optional.of("error")));
    }
}
