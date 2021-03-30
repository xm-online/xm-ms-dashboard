package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.ms.dashboard.domain.Widget;

import java.util.List;

public interface WidgetPermittedRepository  {

    List<Widget> findAll(String privilegeKey);

    List<Widget> findByDashboardId(Long id, String privilegeKey);

}
