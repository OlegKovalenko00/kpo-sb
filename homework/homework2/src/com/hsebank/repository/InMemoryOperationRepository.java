package com.hsebank.repository;


import com.hsebank.domain.Operation;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class InMemoryOperationRepository implements OperationRepository {
    private final Map<String, Operation> map = new ConcurrentHashMap<>();


    public Operation save(Operation op) { map.put(op.getId(), op); return op; }
    public Optional<Operation> findById(String id) { return Optional.ofNullable(map.get(id)); }
    public Collection<Operation> findAll() { return Collections.unmodifiableCollection(map.values()); }
    public void delete(String id) { map.remove(id); }
}