package com.prohgmena_themata;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static List<MyThread> myThreads = new ArrayList<>();

    public static void main(String[] args) {

        // list of input threads (name, duration and dependencies)
        myThreads = getThreadsFromFiles(args);

        // Start all threads
        myThreads.forEach(Thread::start);

        // Wait for all threads to finish
        myThreads.forEach(myThread -> {
            try {
                myThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Waiting for blockchain to be calculated...");

        // Create blocks
        BlockChainController blockChainController = new BlockChainController();
        MyThread.finishedThreads.forEach(blockChainController::createBlock);

        // Check if blockchain is valid
        List<Block> blockChain = BlockChainController.blockChain;
        if (!new BlockChainController().isChainValid(blockChain)) {
            System.out.println("Block Chain is not valid");
            return;
        }

        // Show blockchain
        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println("Your blockchain is:");
        System.out.println(blockChainJson);

        // Store blockchain
        DBConnection dbConnection = new DBConnection();
        dbConnection.addBlockChain(blockChain);
    }

    /**
     * Returns a list of MyThread objects from input files
     *
     * @param args command arguments with the names of input files
     * @return List<MyThread>
     */
    private static List<MyThread> getThreadsFromFiles(String[] args) {
        List<MyThread> myThreads = new ArrayList<>();

        try {
            File precedenceFile = new File("input/" + args[0]);
            File timingsFile = new File("input/" + args[1]);
            Scanner myReader = new Scanner(precedenceFile);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(" ");
                MyThread myThread = new MyThread.Builder(data[0]).build();
                if (data.length > 2) {
                    myThread.setDependencies(data[2].split(","));
                }
                myThreads.add(myThread);
            }
            myReader = new Scanner(timingsFile);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(" ");
                MyThread storedThread = myThreads.stream()
                        .filter(myThread -> data[0].equals(myThread.getThreadName()))
                        .findFirst()
                        .orElse(null);
                if (storedThread != null) {
                    storedThread.setDuration(data.length > 1 ? Long.parseLong(data[1]) : 0L);
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong number of files");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the files");
            e.printStackTrace();
        }

        return myThreads;
    }
}
