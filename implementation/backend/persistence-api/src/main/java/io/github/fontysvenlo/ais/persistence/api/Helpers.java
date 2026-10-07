package io.github.fontysvenlo.ais.persistence.api;

public interface Helpers {

    public default String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
