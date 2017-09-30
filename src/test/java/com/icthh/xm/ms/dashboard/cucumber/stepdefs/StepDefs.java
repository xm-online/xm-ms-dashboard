package com.icthh.xm.ms.dashboard.cucumber.stepdefs;

import com.icthh.xm.ms.dashboard.DashboardApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = DashboardApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
