package org.vniizht.webapp.usercheck;

import javax.naming.NamingException;

class UserCheckSession extends Thread {

    private volatile boolean isRunning = true;

    public final String ip;
    public final UserCheck userCheck;

    public UserCheckSession(String ip) throws NamingException {
        this.ip = ip;
        this.userCheck = new UserCheck();
        this.start();
    }

    public void run() {
        while (isRunning) {
            isRunning = false;
            sleep15minutes();
        }
        exit();
    }

    public void prolong() {
        isRunning = true;
    }

    private void sleep15minutes(){
        try {
            Thread.sleep(15 * 60 * 1000); // 15 minutes
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void exit(){
        try {
            userCheck.exit();
            UserCheckManager.sessionsMap.remove(ip);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
