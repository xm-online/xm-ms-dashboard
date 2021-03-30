package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.commons.permission.service.PermissionCheckService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ConfigPermittedRepository {

    private final PermissionCheckService permissionService;

    protected ConfigPermittedRepository(
        PermissionCheckService permissionService) {
        this.permissionService = permissionService;
    }

    public Boolean matchSpelExpression(String privilegeKey, Object obj) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String condition = permissionService.createCondition(authentication, privilegeKey, new PermissionTranslator());

        if (StringUtils.isBlank(condition)) {
            return Boolean.TRUE;
        }

        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression expression = expressionParser.parseExpression(condition);

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("returnObject", obj);
        Object value = expression.getValue(context);
        return (Boolean) value;
    }

}
