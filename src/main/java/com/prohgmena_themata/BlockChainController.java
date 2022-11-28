package com.prohgmena_themata;

import java.util.ArrayList;
import java.util.List;

public class BlockChainController {

    public static final int BLOCK_PREFIX = 4;
    public static final List<Block> blockChain = new ArrayList<>();

    /**
     * This method creates a block in the blockchain
     *
     * @param myThread
     */
    public void createBlock(MyThread myThread) {
        String simulationName;
        String lastHash;
        if (blockChain.size() > 0) {
            simulationName = blockChain.get(blockChain.size() - 1).getSimulationName();
            lastHash = blockChain.get(blockChain.size() - 1).getHash();
        } else {
            DBConnection dbConnection = new DBConnection();
            simulationName = dbConnection.getSimulationName();
            lastHash = dbConnection.getLastHash();
        }
        Block block = new Block.Builder(lastHash)
                .simulationName(simulationName)
                .threadName(myThread.getThreadName())
                .threadDuration(myThread.getDuration())
                .threadDependencies(myThread.getDependencies())
                .timeStamp(myThread.getTimestamp())
                .build();

        block.mineBlock();
        blockChain.add(block);
    }

    /**
     * This method checks if a blockchain is valid
     *
     * @param blockChain
     * @return Boolean
     */
    public Boolean isChainValid(List<Block> blockChain){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[BLOCK_PREFIX]).replace('\0','0');
        for (int i = 1; i < blockChain.size(); i++){
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);
            if (!currentBlock.getHash().equals(currentBlock.calculateBlockHash())){
                System.out.println("Current Hashes not equal");
                return false;
            }
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())){
                System.out.println("Previous hash not valid");
                return false;
            }
            if (!currentBlock.getHash().substring(0, BLOCK_PREFIX).equals(hashTarget)){
                System.out.println("Block is not properly mined");
                return false;
            }
        }
        return true;
    }
}
