package com.domain;

import javax.xml.validation.ValidatorHandler;
import java.util.Objects;

public abstract class Entity <ID  extends com.domain.UniqueIdentifier> {
    protected final ID id;
    
    protected Entity(final ID id) {
        this.id = id;
    }
    
    public abstract void validate(ValidatorHandler handler);

    public ID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
