package com.med.viral.repository;

import com.med.viral.model.Action;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends CrudRepository<Action, Integer> {
}
