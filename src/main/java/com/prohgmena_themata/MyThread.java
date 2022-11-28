package com.prohgmena_themata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MyThread extends Thread{
    private final String threadName;
    private long duration;
    private String[] dependencies;
    private long timestamp;
    public static final ArrayList<MyThread> finishedThreads = new ArrayList<>();

    private MyThread(Builder builder) {
        this.threadName = builder.threadName;
        this.duration = builder.duration;
        this.dependencies = builder.dependencies;
    }

    public static class Builder {
        private final String threadName;
        private long duration;
        private String[] dependencies;

        public Builder(String threadName) {
            this.threadName = threadName;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder dependencies (String[] dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public MyThread build() {
            return new MyThread(this);
        }
    }

    @Override
    public void run() {
        String name = this.getThreadName();
        long duration = this.getDuration();
        String dependenciesText = this.getDependencies() != null ? " and waited for " + String.join(",", dependencies) : "";
        String[] dependencies = this.getDependencies() != null ? this.getDependencies() : new String[]{};

        // Wait for dependent threads to finish
        Main.myThreads.forEach(myThread -> {
            if (Arrays.asList(dependencies).contains(myThread.getThreadName())) {
                try {
                    myThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // The execution time of the thread
        if (duration > 0) {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Set the timestamp the thread executed
        this.setTimestamp(new Date().getTime());

        synchronized (finishedThreads) {
            finishedThreads.add(this);
            System.out.println(name + " executed in " + duration + " ms" + dependenciesText);
        }
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }

    public String getThreadName() {
        return threadName;
    }

    public long getDuration() {
        return duration;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
