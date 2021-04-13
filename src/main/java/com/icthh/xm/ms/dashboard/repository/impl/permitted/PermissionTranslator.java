package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import com.icthh.xm.commons.permission.access.subject.Subject;
import com.icthh.xm.commons.permission.service.translator.SpelTranslator;

public class PermissionTranslator implements SpelTranslator {

    @Override
    public String translate(String spel, Subject subject) {
        return applySubject(spel, subject);
    }
}
