import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    static final int[] QUEUE_CAPACITY = { 2, 3, 5 };
    static final int MAX_CASHIERS = 3;
    static final int MAX_BURGERS = 50;
    private static int currentQueueIndex = 0;
    static final String DATA_FILE_PATH = "Foodies_Fave_Food_Center.txt";
    static boolean shouldExit = false;

    static List<FoodQueue> queues = new ArrayList<>();
    static List<Customer> waitingQueue = new ArrayList<>();
    static int burgersInStock = MAX_BURGERS;

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeQueues();
        displayMenu();
    }

    private static void initializeQueues() {
        for (int i = 0; i < MAX_CASHIERS; i++) {
            queues.add(new FoodQueue(QUEUE_CAPACITY[i]));
        }
    }

    private static void displayMenu() {
        //Display the menu options
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
        System.out.println("110 or IFQ: Print income of each queue");
        System.out.println("999 or EXT: Exit the Program");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println();
        System.out.println("Enter your choice:");
        String option = scanner.nextLine().toUpperCase();

        //Handle the selected option
        switch (option) {
            case "100", "VFQ" -> viewAllQueues();
            case "101", "VEQ" -> viewAllEmptyQueues();
            case "102", "ACQ" -> addCustomerToQueue();
            case "103", "RCQ" -> removeCustomerFromQueue();
            case "104", "PCQ" -> removeServedCustomer();
            case "105", "VCS" -> viewCustomersSorted();
            case "106", "SPD" -> storeProgramData();
            case "107", "LPD" -> loadProgramData();
            case "108", "STK" -> viewRemainingBurgerStock();
            case "109", "AFS" -> addBurgersToStock();
            case "110", "IFQ" -> printIncomeOfEachQueue();
            case "999", "EXT" -> {
                System.out.println("Exiting the program...");
                shouldExit = true;
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }

        System.out.println();

        //Recursively display the menu until the user choose to exit
        if (!shouldExit) {
            displayMenu();
        }
    }

    private static void viewAllQueues() {
        //Call the static method in FoodQueue class to display the queues visually
        FoodQueue.displayQueueVisual(queues);
    }

    private static void viewAllEmptyQueues() {
        boolean emptyQueuesFound = false;
        for (int i = 0; i < queues.size(); i++) {
            FoodQueue queue = queues.get(i);
            if (queue.isEmpty()) {
                System.out.println("Cashier " + (i + 1) + ": Queue is empty");
                emptyQueuesFound = true;
            }
        }
        if (!emptyQueuesFound) {
            System.out.println("No empty queues found.");
        }
    }



    private static void addCustomerToQueue() {
        //Find the queue with the minimum length to add the customer
        FoodQueue minQueue = findQueueWithMinimumLength(queues);

        if (minQueue == null || minQueue.isFull()) {
            System.out.println("All queues are full. Customers are being added to the waiting list.");
            addCustomerToWaitingList();
            return;
        }

        //Get customer details from user input
        System.out.println("Enter the customer's first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter the customer's last name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter the number of burgers required:");
        int burgersRequired = scanner.nextInt();
        scanner.nextLine();

        Customer customer = new Customer(firstName, lastName, burgersRequired);
        minQueue.enqueue(customer);
        burgersInStock -= burgersRequired;

        System.out.println("Customer " + firstName + " " + lastName + " added to queue " + (queues.indexOf(minQueue) + 1));

        if (burgersInStock <= 10) {
            System.out.println("Low stock");
        }

        // Update the currentQueueIndex to point to the next queue in a round-robin fashion
        currentQueueIndex = (currentQueueIndex + 1) % queues.size();
    }

    public static FoodQueue findQueueWithMinimumLength(List<FoodQueue> queues) {
        //Method to find the queue with minimum length queue
        FoodQueue minQueue = null;
        int minLength = Integer.MAX_VALUE;

        for (int i = 0; i < queues.size(); i++) {
            int queueIndex = (currentQueueIndex + i) % queues.size(); // Calculate the queue index in a round-robin fashion
            FoodQueue queue = queues.get(queueIndex);

            int queueLength = queue.getQueueLength();
            if (queueLength < minLength && !queue.isFull()) {
                minLength = queueLength;
                minQueue = queue;
            }
        }

        return minQueue;
    }



    private static void addCustomerToWaitingList() {
        //Get customer details from user input
        System.out.println("Enter the customer's first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter the customer's last name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter the number of burgers required:");
        int burgersRequired = scanner.nextInt();
        scanner.nextLine();

        //Create a new Customer object
        Customer customer = new Customer(firstName, lastName, burgersRequired);

        //Add the customer to the waiting list
        waitingQueue.add(customer);
        System.out.println("Customer " + firstName + " " + lastName + " added to the waiting list.");
    }


    private static void removeServedCustomer() {
        //Get the cashier number from user input
        System.out.println("Enter the cashier number (1-" + MAX_CASHIERS + "):");
        int cashierNumber = scanner.nextInt();
        scanner.nextLine();

        if (cashierNumber < 1 || cashierNumber > MAX_CASHIERS) {
            System.out.println("Invalid cashier number.");
            return;
        }

        int queueIndex = cashierNumber - 1;

        if (queues.get(queueIndex).isEmpty()) {
            System.out.println("Queue " + cashierNumber + " is empty.");
            return;
        }

        //Dequeue the served customer from the selected queue
        Customer servedCustomer = queues.get(queueIndex).dequeue();
        if (servedCustomer != null) {
            System.out.println("Served customer " + servedCustomer.getFullName() + " removed from queue " + cashierNumber);
            burgersInStock -= servedCustomer.getBurgersRequired();

            if (!waitingQueue.isEmpty()) {
                //Add the next customer from the waiting list to the selected queue
                Customer nextCustomer = waitingQueue.remove(0);
                queues.get(queueIndex).enqueue(nextCustomer);
                System.out.println("Next customer " + nextCustomer.getFullName() + " added to queue " + cashierNumber);
            }
        } else {
            System.out.println("Failed to remove served customer from queue " + cashierNumber);
        }
    }

    private static void removeCustomerFromQueue() {
        //Get the cashier number from user input
        System.out.println("Enter the cashier number (1-" + MAX_CASHIERS + "):");
        int cashierNumber = scanner.nextInt();
        scanner.nextLine();

        if (cashierNumber < 1 || cashierNumber > MAX_CASHIERS) {
            System.out.println("Invalid cashier number.");
            return;
        }

        int queueIndex = cashierNumber - 1;

        if (queues.get(queueIndex).isEmpty()) {
            System.out.println("Queue " + cashierNumber + " is empty.");
            return;
        }

        //Get the position of the customer to remove from user input
        int position = scanner.nextInt();
        scanner.nextLine();

        if (position < 1 || position > queues.get(queueIndex).getCustomers().size()) {
            System.out.println("Invalid position.");
            return;
        }

        //Remove the customer from the selected queue at the specified position
        boolean removed = queues.get(queueIndex).dequeue(position);
        if (removed) {
            System.out.println("Customer removed from queue " + cashierNumber);
            if (!waitingQueue.isEmpty()) {
                //Add the next customer from the waiting list to the selected queue
                Customer nextCustomer = waitingQueue.remove(0);
                queues.get(queueIndex).enqueue(nextCustomer);
                System.out.println("Next customer " + nextCustomer.getFullName() + " added to queue " + cashierNumber);
            }
        } else {
            System.out.println("Failed to remove customer from queue " + cashierNumber);
        }
    }

    private static void viewCustomersSorted() {
        //Create a set to store unique customers from all queues
        Set<Customer> uniqueCustomers = new HashSet<>();
        for (FoodQueue queue : queues) {
            uniqueCustomers.addAll(queue.getCustomers());
        }

        //Convert the set to a list and sort it alphabetically
        List<Customer> sortedCustomers = new ArrayList<>(uniqueCustomers);
        Collections.sort(sortedCustomers, Comparator.comparing(Customer::getFullName));

        System.out.println("Customers sorted in alphabetical order:");
        for (Customer customer : sortedCustomers) {
            System.out.println(customer.getFullName());
        }
    }


    private static void storeProgramData() {
        //Method to store data into the text file
        try {
            FileWriter fileWriter = new FileWriter(DATA_FILE_PATH);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            //Write the burgersInStock and queue data to the file
            printWriter.println("Burgers in stock: " + burgersInStock);

            for (int i = 0; i < queues.size(); i++) {
                FoodQueue queue = queues.get(i);
                printWriter.println("Queue " + (i + 1) + ": " + queue.getQueueString());
            }

            printWriter.println();

            printWriter.close();
            System.out.println("Program data stored successfully.");
        } catch (IOException e) {
            System.out.println("Error storing program data: " + e.getMessage());
        }
    }

    private static void loadProgramData() {
        //Method to load the data from file
        try {
            File file = new File(DATA_FILE_PATH);
            Scanner fileScanner = new Scanner(file);

            //Read the file line by line and update the program data accordingly
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.startsWith("Burgers in stock: ")) {
                    burgersInStock = Integer.parseInt(line.split(": ")[1]);
                } else if (line.startsWith("Queue ")) {
                    int queueIndex = Integer.parseInt(line.substring(6, 7)) - 1;
                    FoodQueue queue = queues.get(queueIndex);
                    String customersData = line.substring(9);

                    //Split the customers' data and add them to the queue
                    if (!customersData.isEmpty()) {
                        String[] customers = customersData.split(";");
                        for (String customer : customers) {
                            String[] customerDetails = customer.split(",");
                            String fullName = customerDetails[0];
                            int burgersRequired = Integer.parseInt(customerDetails[1]);
                            String[] nameParts = fullName.split(" ");
                            String firstName = nameParts[0];
                            String lastName = nameParts[1];

                            Customer newCustomer = new Customer(firstName, lastName, burgersRequired);
                            queue.enqueue(newCustomer);
                        }
                    }
                }
            }

            fileScanner.close();
            System.out.println("Program data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error loading program data: " + e.getMessage());
        }
    }

    private static void viewRemainingBurgerStock() {
        //Method to view remaining burgers in stock
        System.out.println("Remaining burgers in stock: " + burgersInStock);
    }

    private static void addBurgersToStock() {
        //Get the number of burgers to add from user input
        System.out.println("Enter the number of burgers to add to stock:");
        int burgersToAdd = scanner.nextInt();
        scanner.nextLine();

        if (burgersToAdd < 0) {
            System.out.println("Invalid number of burgers. Cannot add a negative quantity.");
            return;
        }

        //Update burgerInStock with the added burgers
        burgersInStock += burgersToAdd;
        System.out.println(burgersToAdd + " burgers added to stock. Total burgers in stock: " + burgersInStock);
    }

    private static void printIncomeOfEachQueue() {
        //Get the burgers income of each queue
        System.out.println("Income of each queue:");

        for (int i = 0; i < queues.size(); i++) {
            FoodQueue queue = queues.get(i);
            int queueIncome = queue.getCustomers().stream().mapToInt(Customer::getBurgersRequired).sum() * 650;
            System.out.println("Queue " + (i + 1) + ": " + queueIncome);
        }
    }
}