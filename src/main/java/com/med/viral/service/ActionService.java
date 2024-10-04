package com.med.viral.service;

import com.med.viral.model.Action;

import java.util.List;


public interface ActionService {
    List<Action> getActions(Integer adminId);

    Action saveAction(Action action);
}
