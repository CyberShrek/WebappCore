package org.vniizht.forge.webapp.model;

import com.vniizht.ucheck.UserCheckRemote;

import java.util.Arrays;

public class User {

    private final UserCheckRemote remote;

    public final boolean superUser;
    public final int carrier;
    public final String country;
    public final String road;
    public final int agent;
    public final boolean create;
    public final boolean read;
    public final boolean update;
    public final boolean delete;
    public final boolean load;
    public final boolean download;

    public User(UserCheckRemote remote) throws Exception {
        this.remote = remote;
        this.superUser = permits("create", "read", "update", "delete", "load", "download");
        this.carrier = remote.getSkp();
        this.country = remote.getUserGos();
        this.road = remote.getUserDor();
        this.agent = remote.getAgent();
        this.create = permits("create");
        this.read = permits("read");
        this.update = permits("update");
        this.delete = permits("delete");
        this.load = permits("load");
        this.download = permits("download");
    }

    private boolean permits(String... operations) {
        return Arrays.stream(operations).allMatch(operation -> {
            try {
                return remote.getParamI(operation) != 0;
            } catch (Exception e) {
                return false;
            }
        });
    }
}