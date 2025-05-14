package com.empresa.clientefrontend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
public class Logradouro {

    private Long id;
    private String logradouro;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Logradouro)) return false;
        Logradouro that = (Logradouro) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
