package com.salihapp.keycloak.config;

public final class ApiPaths {
    public static final String BASE_API = "/api/v1";

    private ApiPaths() {
        throw new AssertionError("No instances of ApiPaths allowed.");
    }

    public static final class Keycloak {

        public static final String BASE = BASE_API + "/keycloak";

        public static final String ADMIN = "/admin";
        public static final String USER  = "/user";
        public static final String HOME  = "/home";

        private Keycloak() {
            throw new AssertionError("No instances of Keycloak allowed.");
        }
    }
}