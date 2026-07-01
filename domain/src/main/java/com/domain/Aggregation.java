package com.domain;

public abstract class Aggregation<ID extends com.domain.UniqueIdentifier> extends com.domain.Entity<ID> {
    protected Aggregation(final ID id) {
        super(id);
    }
}
