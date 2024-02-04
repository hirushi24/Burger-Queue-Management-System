import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class ArrayVersion {
    private static final int[] QUEUE_CAPACITY = {2, 3, 5};
    private static final int MAX_QUEUE_SIZE = 5;
    private static final int MAX_CASHIERS = 3;
    private static final int MAX_BURGERS = 50;
    private static final String DATA_FILE_PATH = "Foodies_Fave_Food_Center.txt";
    private static boolean shouldExit = false;


    private static boolean[][] queues = new boolean[MAX_CASHIERS][MAX_QUEUE_SIZE]; //Array to represent the queues
    private static String[][] queueNames = new String[MAX_CASHIERS][MAX_QUEUE_SIZE]; //Array to store customer names in each queue
    private static int[] queueSize = {0, 0, 0}; //Array to store the  size of each queue
    private static int burgersInStock = MAX_BURGERS; //Number of burgers in stock

    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        initializeQueues();
        displayMenu();
    }

    //Initializes the queues with empty values
    private static void initializeQueues() {
        for (int i = 0; i < MAX_CASHIERS; i++) {
            queues[i] = new boolean[QUEUE_CAPACITY[i]];
            queueNames[i] = new String[QUEUE_CAPACITY[i]];
        }
    }

    // Displays the menu options and handles user input
    private static void displayMenu() {
        System.out.println();
        System.out.println("-----------------------------Menu Options-----------------------------");
        System.out.println("100 or VFQ: View all Queues");
        System.out.println("101 or VEQ: View all Empty Queues");
        System.out.println("102 or ACQ: Add customer to a Queue");
        System.out.println("103 or RCQ: Remove a customer from a Queue (From a specific location)");
        System.out.println("104 or PCQ: Remove a served customer");
        System.out.println("105 or VCS: View Customers Sorted in alphabetical order");
        System.out.println("106 or SPD: Store Program Data into file");
        System.out.println("107 or LPD: Load Program Data from file");
        System.out.println("108 or STK: View Remaining burgers Stock");
        System.out.println("109 or AFS: Add burgers to Stock");
        System.out.println("999 or EXT: Exit the Program");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println();
        System.out.println("Enter your choice:");
        String option = scanner.next().toUpperCase();
        scanner.nextLine();


        // Handle user input based on the chosen option
        switch (option) {
            case "100","VFQ" -> viewAllQueues();
            case "101","VEQ" -> viewAllEmptyQueues();
            case "102","ACQ" -> addCustomerToQueue();
            case "103","RCQ" -> removeCustomerFromQueue();
            case "104","PCQ" -> removeServedCustomer();
            case "105","VCS" -> viewCustomerSorted();
            case "106","SPD" -> storeProgramData();
            case "107","LPD" -> loadProgramData();
            case "108","STK" -> viewRemainingBurgerStock();
            case "109","AFS" -> addBurgerToStock();
            case "999","EXT" -> {
                System.out.println("Exiting the program...");
                shouldExit = true;
            }
            default -> System.out.println("Invalid choice.Please try again.");
        }
        System.out.println();

        if(!shouldExit) {
            displayMenu();
        }
    }

    private static void viewAllQueues() {
        System.out.println("*****************");
        System.out.println("* Cashiers *");
        System.out.println("*****************");

        int maxQueueCapacity = getMaxQueueCapacity();

        for (int j = 0; j < maxQueueCapacity; j++) {
            for (int i = 0; i < MAX_CASHIERS; i++) {
                if (j < QUEUE_CAPACITY[i]) {
                    if (queues[i][j]) {
                        System.out.print("O ");     // Customer present in the queue
                    } else {
                        System.out.print("X ");     // Queue is empty
                    }
                } else {
                    System.out.print("  ");         // Empty spaces for queues with lower capacity
                }
            }
            System.out.println();
        }
    }

    private static int getMaxQueueCapacity() {
        int maxCapacity = 0;
        for (int i = 0; i < MAX_CASHIERS; i++) {
            if (QUEUE_CAPACITY[i] > maxCapacity) {
                maxCapacity = QUEUE_CAPACITY[i];
            }
        }
        return maxCapacity;

    }


    private static void viewAllEmptyQueues() {
        int emptyQueueCount = 0;
        for (int i = 0; i < MAX_CASHIERS; i++) {
            if (queueSize[i] == 0) {
                System.out.println("Cashier" + (i + 1) + ": Queue is empty");
                emptyQueueCount++;
            }
        }
        if(emptyQueueCount==0){
            System.out.println("No empty queues found.");
        }
        // This method displays the number of all empty queues or notifies if no empty queue is found.
    }


    private static void addCustomerToQueue() {
        System.out.println("Enter the cashier number (1-" + MAX_CASHIERS + "):");
        int cashierNumber = scanner.nextInt();
        scanner.nextLine();

        if (cashierNumber < 1 || cashierNumber > MAX_CASHIERS) {
            System.out.println("Invalid cashier number.");
            return;
        }

        int queueIndex = cashierNumber - 1;
        if (queueSize[queueIndex] == QUEUE_CAPACITY[queueIndex]) {
            System.out.println("Queue " + cashierNumber + " is already full.");
            return;
        }

        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();

        int customerIndex = queueSize[queueIndex];
        queues[queueIndex][customerIndex] = true;
        if (queueNames[queueIndex][customerIndex] == null) {
            queueNames[queueIndex][customerIndex] = customerName;
        } else {
            queueNames[queueIndex][customerIndex] += ", " + customerName;
        }
        queueSize[queueIndex]++;
        System.out.println("Customer " + customerName + " added to queue " + cashierNumber);

        System.out.println();

        if (burgersInStock<=10){
            System.out.println("Low stock");
        }
        // This method allows adding a customer to a specific cashier queue, if the queue is not already full.
    }


    private static void removeCustomerFromQueue () {
        System.out.println("Enter the cashier number (1-" + MAX_CASHIERS + "):");
        int cashierNumber = scanner.nextInt();
        scanner.nextLine();

        if (cashierNumber < 1 || cashierNumber > MAX_CASHIERS) {
            System.out.println("Invalid cashier number.");
            return;
        }

        int queueIndex = cashierNumber - 1;
        if (queueSize[queueIndex] == 0) {
            System.out.println("Queue " + cashierNumber + " is empty.");
            return;
        }

        System.out.println("Enter the position of the customer to remove (1-" + queueSize[queueIndex] + "):");
        int position = scanner.nextInt();
        scanner.nextLine();

        if (position < 1 || position > queueSize[queueIndex]) {
            System.out.println("Invalid position.");
            return;
        }

        String customerName = queueNames[queueIndex][position - 1];
        queues[queueIndex][position - 1] = false;
        queueNames[queueIndex][position - 1] = null;

        for (int i = position - 1; i < queueSize[queueIndex] - 1; i++) {
            queues[queueIndex][i] = queues[queueIndex][i + 1];
            queueNames[queueIndex][i] = queueNames[queueIndex][i + 1];
        }

        queueSize[queueIndex]--;

        System.out.println("Customer " + customerName + " removed from queue " + cashierNumber);
        // This method allows removing a customer from a specific cashier queue at a given position.
    }



    private static void removeServedCustomer() {
        System.out.println("Enter the cashier number (1-" + MAX_CASHIERS + "):");
        int cashierNumber = scanner.nextInt();
        scanner.nextLine();

        if (cashierNumber < 1 || cashierNumber > MAX_CASHIERS) {
            System.out.println("Invalid cashier number.");
            return;
        }

        int queueIndex = cashierNumber - 1;
        if (queueSize[queueIndex] == 0) {
            System.out.println("Queue " + cashierNumber + " is empty.");
            return;
        }

        String servedCustomer = queueNames[queueIndex][0];

        // Shift the queue elements to remove the served customer
        System.arraycopy(queues[queueIndex], 1, queues[queueIndex], 0, queueSize[queueIndex] - 1);
        System.arraycopy(queueNames[queueIndex],1, queueNames[queueIndex], 0, queueSize[queueIndex] - 1);


        queues[queueIndex][queueSize[queueIndex] - 1] = false;
        queueNames[queueIndex][queueSize[queueIndex] - 1] = null;
        queueSize[queueIndex]--;
        burgersInStock-=5;

        System.out.println("Served customer " + servedCustomer + " removed from queue " + cashierNumber);
        // This method removes the first customer (served customer ) from a specific cashier queue.
    }


    private static void viewCustomerSorted() {
        String[] customers = new String[10];
        int customerCount = 0;

        // Collect all customer names from the queues
        for (int i = 0; i < MAX_CASHIERS; i++) {
            for (int j = 0; j < QUEUE_CAPACITY[i]; j++) {
                if (queues[i][j]) {
                    customers[customerCount] = queueNames[i][j];
                    customerCount++;
                }
            }
        }

        // Sort the customers array in alphabetical order
        for (int i = 0; i < customerCount - 1; i++) {
            for (int j = 0; j < customerCount - i - 1; j++) {
                if (customers[j].compareTo(customers[j + 1]) > 0) {
                    String temp = customers[j];
                    customers[j] = customers[j + 1];
                    customers[j + 1] = temp;
                }
            }
        }

        System.out.println("Customers sorted in alphabetical order:");
        for (int i = 0; i < customerCount; i++) {
            System.out.println(customers[i]);
        }
        // This method collects all the customer names from all queues, sorts them in alphabetical order, and then displays them.
    }



    private static void storeProgramData() {
        try {
            FileWriter fileWriter = new FileWriter(DATA_FILE_PATH);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("Burgers in stock :" + burgersInStock);

            for (int i = 0; i < MAX_CASHIERS; i++) {
                printWriter.println("Queue " + (i + 1) + ":");
                for (int j = 0; j < queueSize[i]; j++) {
                    printWriter.println(queueNames[i][j]);
                }
            }
            printWriter.println();

            printWriter.close();
            System.out.println("Program data stored successfully.");
        } catch (IOException e) {
            System.out.println("Error storing program data: " + e.getMessage());
        }
    }



    private static void loadProgramData() {
        try {
            File file = new File(DATA_FILE_PATH);
            Scanner scanner = new Scanner(file);

            while(scanner.hasNextLine()){
                String program_data = scanner.nextLine();
                System.out.println(program_data);
            }

            scanner.close();
            System.out.println("Program data loaded successfully.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // This method is used to load the program data from a file. It retrieves the saved state of the queues, customer names, and burgers in stock.
    }


    private static void viewRemainingBurgerStock(){
        System.out.println("Remaining burgers in stock:"+ burgersInStock);
        // This method displays the current number of burgers in stock.
    }


    private static void addBurgerToStock() {
        System.out.println("Enter the number of burgers to add to stock:");
        int burgersToAdd = scanner.nextInt();
        scanner.nextLine();

        if (burgersToAdd < 0) {
            System.out.println("Invalid number of burgers. Cannot add a negative quantity.");
            return;
        }

        burgersInStock += burgersToAdd;
        System.out.println(burgersToAdd + "burgers added to stock. Total burgers in stock:" + burgersInStock);
    }
    //This method allows adding a specified number of burgers to the stock.
}


