package com.hsebank.repository;


import com.hsebank.domain.Operation;


import java.util.Collection;
import java.util.Optional;


public interface OperationRepository {
    Operation save(Operation op);
    Optional<Operation> findById(String id);
    Collection<Operation> findAll();
    void delete(String id);
}