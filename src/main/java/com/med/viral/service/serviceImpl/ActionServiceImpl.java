package com.med.viral.service.serviceImpl;

import com.med.viral.model.Action;
import com.med.viral.repository.ActionRepository;
import com.med.viral.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {
    private final ActionRepository actionRepository;

    @Override
    public List<Action> getActions(Integer adminId) {
        return actionRepository.findAll().stream().filter(a -> a.getCreatedBy().equals(adminId)).toList();
    }

    @Override
    public Action saveAction(Action action) {
        return actionRepository.save(action);
    }
}
