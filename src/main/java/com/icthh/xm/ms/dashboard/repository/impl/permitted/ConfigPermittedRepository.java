package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ConfigPermittedRepository {

    private final PermissionCheckService permissionService;
    private volatile ExpressionParser expressionParser;
    private final Map<String, Expression> expressions = new ConcurrentHashMap<>();

    protected ConfigPermittedRepository(
        PermissionCheckService permissionService) {
        this.permissionService = permissionService;
        this.expressionParser = createParser(SpelParserConfiguration.DEFAULT_MAX_EXPRESSION_LENGTH);
    }

    // Multi-role conditions are OR-joined and can exceed Spring's default SpEL length limit
    @Autowired(required = false)
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.expressionParser = createParser(applicationProperties.getSpelMaxExpressionLength());
        this.expressions.clear();
    }

    private ExpressionParser createParser(int maxExpressionLength) {
        SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.MIXED,
                                                                     this.getClass().getClassLoader(),
                                                                     false, false,
                                                                     Integer.MAX_VALUE,
                                                                     maxExpressionLength);
        return new SpelExpressionParser(config);
    }

    public Boolean matchSpelExpression(String privilegeKey, Object obj) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String condition = permissionService.createCondition(authentication, privilegeKey, new PermissionTranslator());
        if (StringUtils.isBlank(condition)) {
            return Boolean.TRUE;
        }
        Expression expression = expressions.computeIfAbsent(condition, expressionParser::parseExpression);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("returnObject", obj);
        Object value = expression.getValue(context);
        return (Boolean) value;
    }

}
