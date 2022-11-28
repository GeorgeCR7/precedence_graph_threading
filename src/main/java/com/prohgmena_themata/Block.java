package com.prohgmena_themata;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Block {

    private String hash;
    private final String previousHash;
    private final String simulationName;
    private final String threadName;
    private final long threadDuration;
    private final String[] threadDependencies;
    private final long timeStamp;
    private int nonce;

    private Block(Builder builder) {
        this.simulationName = builder.simulationName;
        this.threadName = builder.threadName;
        this.threadDuration = builder.threadDuration;
        this.threadDependencies = builder.threadDependencies;
        this.timeStamp = builder.timeStamp;
        this.previousHash = builder.previousHash;
        this.hash = this.calculateBlockHash();

    }

    // Utilize the builder design pattern
    public static class Builder {
        private final String previousHash;
        private String simulationName;
        private String threadName;
        private long threadDuration;
        private String[] threadDependencies;
        private long timeStamp;

        public Builder(String previousHash) {
            this.previousHash = previousHash;
        }

        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public Builder simulationName(String simulationName) {
            this.simulationName = simulationName;
            return this;
        }

        public Builder threadDuration(long threadDuration) {
            this.threadDuration = threadDuration;
            return this;
        }

        public Builder threadDependencies(String[] threadDependencies) {
            this.threadDependencies = threadDependencies;
            return this;
        }

        public Builder timeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Block build() {
            return new Block(this);
        }
    }

    /**
     * Method to mine a block
     *
     * @return String
     */
    public String mineBlock(){
        String prefixString = new String(new char[BlockChainController.BLOCK_PREFIX]).replace('\0','0');
        while (!hash.substring(0, BlockChainController.BLOCK_PREFIX).equals(prefixString)){
            nonce++;
            hash = calculateBlockHash();
        }
        return hash;
    }

    /**
     * Method to calculate block hash based on input data
     *
     * @return String
     */
    public String calculateBlockHash(){
        String dataToHash = previousHash +
                timeStamp +
                nonce +
                simulationName +
                threadName +
                threadDuration +
                Arrays.toString(threadDependencies);
        MessageDigest digest = null;
        byte[] bytes = null;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        StringBuilder buffer = new StringBuilder();
        assert bytes != null;
        for (byte b: bytes)
            buffer.append(String.format("%02x",b));

        return buffer.toString();
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public String getThreadName() {
        return threadName;
    }

    public long getThreadDuration() {
        return threadDuration;
    }

    public String[] getThreadDependencies() {
        return threadDependencies;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }
}
