package mcjty.enigma.code;

import javax.annotation.Nonnull;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public final class ScopeID {
    @Nonnull private final Integer id;

    public ScopeID(@Nonnull Integer id) {
        this.id = id;
    }

    public ScopeID(@Nonnull String id) {
        this.id = STRINGS.get(id);
    }

    @Nonnull
    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScopeID scopeID = (ScopeID) o;

        if (!id.equals(scopeID.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
