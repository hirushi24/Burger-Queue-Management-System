package com.example.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class GUI extends Application {

    static TextField firstNameField;
    static TextField lastNameField;
    static TextArea foodQueueTextArea;
    static TextArea waitingQueueTextArea;
    static TextArea searchResults;
    static Scanner scanner = new Scanner(System.in);

    static List<Customer> Queue_1 = new ArrayList<>();
    static List<Customer> Queue_2 = new ArrayList<>();
    static List<Customer> Queue_3 = new ArrayList<>();


    @Override
    public void start(Stage stage) {
        boolean shouldExit = false;
        while (!shouldExit) {
            displayMenu();
            String option = scanner.nextLine().toUpperCase();
            switch (option) {
                case "102", "ACQ" -> FoodQueue.addCustomerToQueue();
                case "103", "RCQ" -> FoodQueue.removeCustomerFromQueue();
                case "104", "PCQ" -> FoodQueue.removeServedCustomer();
                case "999", "EXT" -> {
                    System.out.println("Exiting the program...");
                    shouldExit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }

        stage.setTitle("Food Queue Application.");
        firstNameField = new TextField();
        lastNameField = new TextField();

        foodQueueTextArea = new TextArea();
        foodQueueTextArea.setEditable(false);

        waitingQueueTextArea = new TextArea();
        waitingQueueTextArea.setEditable(false);

        searchResults = new TextArea();
        searchResults.setEditable(false);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> FoodQueue.refreshQueues());
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> FoodQueue.searchingTheCustomer());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Food Queues: "), 0, 0);
        gridPane.add(foodQueueTextArea, 0, 1);
        gridPane.add(new Label("Waiting Queue: "), 1, 0);
        gridPane.add(waitingQueueTextArea, 1, 1);
        gridPane.add(refreshButton, 0, 2);
        gridPane.add(new Label("Search customer"), 0, 3);
        gridPane.add(new Label("First name:"), 0, 4);
        gridPane.add(firstNameField, 1, 4);
        gridPane.add(new Label("Second name:"), 0, 5);
        gridPane.add(lastNameField, 1, 5);


        gridPane.add(searchButton, 0, 6);
        gridPane.add(searchResults, 0, 7);

        Scene scene = new Scene(gridPane, 800, 400);
        stage.setScene(scene);
        stage.show();
        FoodQueue.refreshQueues();
    }

    public static void displayMenu() {
        //Display the menu options
        System.out.println("""
                102 or ACQ: Add customer to a Queue.
                103 or RCQ: Remove a customer from a Queue.
                104 or PCQ: Remove a served customer.
                999 or EXT: Exit and view the Food Queues.
                Enter your choice:""");
    }

    public static void main(String[] args) {
        launch();
    }
}

class FoodQueue {
    private int capacity;
    private Queue<Customer> customers;
    private int size;
    static final int[] QUEUE_CAPACITY = {2, 3, 5};
    static final int MAX_CASHIERS = 3;
    static final int MAX_BURGERS = 50;
    static List<FoodQueue> queues = new ArrayList<>();
    static List<Customer> waitingQueue = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public FoodQueue(int capacity) {
        this.capacity = capacity;
        this.customers = new LinkedList<>();
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public boolean isFull() {
        return customers.size() == capacity;
    }

    public void enqueue(Customer customer) {
        customers.add(customer);
        System.out.println("Customer " + customer.getFirstName() + " " + customer.getLastName() + " added to the queue.");
    }

    public boolean dequeue(int position) {
        if (position >= 1 && position <= customers.size()) {
            int count = 1;
            Iterator<Customer> iterator = customers.iterator();

            while (iterator.hasNext()) {
                iterator.next();
                if (count == position) {
                    iterator.remove();
                    return true;
                }
                count++;
            }
        }
        return false;
    }

    public Customer dequeue() {
        return customers.poll();
    }

    public String toString() {
        return customers.toString();
    }

    public String getQueueString() {
        StringBuilder sb = new StringBuilder();
        for (Customer customer : customers) {
            sb.append(customer.getFullName()).append(",").append(customer.getBurgersRequired()).append(";");
        }
        return sb.toString();
    }

    public static void addCustomerToQueue() {
        //Add customer to a queue
        System.out.println("Enter the customer's first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter the customer's last name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter the number of burgers required:");
        int burgersRequired = scanner.nextInt();
        scanner.nextLine();

        boolean allQueuesFull = queues.stream().allMatch(FoodQueue::isFull);

        if (GUI.Queue_1.size() + GUI.Queue_2.size() + GUI.Queue_3.size() < 10) {
            Customer customer = new Customer(firstName, lastName, burgersRequired);
            if (GUI.Queue_1.size() < QUEUE_CAPACITY[0]) {
                GUI.Queue_1.add(customer);
                System.out.println("Customer " + firstName + " " + lastName + " added to Queue 1");
            } else if (GUI.Queue_2.size() < QUEUE_CAPACITY[1]) {
                GUI.Queue_2.add(customer);
                System.out.println("Customer " + firstName + " " + lastName + " added to Queue 2");
            } else if (GUI.Queue_3.size() < QUEUE_CAPACITY[2]) {
                GUI.Queue_3.add(customer);
                System.out.println("Customer " + firstName + " " + lastName + " added to Queue 3");
            }
        } else if (allQueuesFull) {
            Customer customer = new Customer(firstName, lastName, burgersRequired);
            waitingQueue.add(customer);
            System.out.println("All queues are full. Customers are being added to the waiting list.");
        } else {
            FoodQueue minQueue = findQueueWithMinimumLength(queues);
            if (minQueue.isFull()) {
                Customer customer = new Customer(firstName, lastName, burgersRequired);
                waitingQueue.add(customer);
                System.out.println("Queue " + (queues.indexOf(minQueue) + 1) + " is full. Customer added to the waiting list.");
            } else {
                Customer customer = new Customer(firstName, lastName, burgersRequired);
                minQueue.enqueue(customer);
                System.out.println("Customer " + firstName + " " + lastName + " added to Queue " + (queues.indexOf(minQueue) + 1));
            }
        }
    }

    public static void removeCustomerFromQueue() {
        //Remove a customer from a queue
        System.out.println("Enter the queue number (1-3):");
        int queueNumber = scanner.nextInt();
        scanner.nextLine();

        if (queueNumber < 1 || queueNumber > 3) {
            System.out.println("Invalid queue number.");
            return;
        }

        List<Customer> targetQueue = null;
        switch (queueNumber) {
            case 1:
                targetQueue = GUI.Queue_1;
                break;
            case 2:
                targetQueue = GUI.Queue_2;
                break;
            case 3:
                targetQueue = GUI.Queue_3;
                break;
        }

        if (targetQueue == null || targetQueue.isEmpty()) {
            System.out.println("Queue " + queueNumber + " is empty.");
            return;
        }

        System.out.println("Enter the position (1-" + targetQueue.size() + ") of the customer to remove:");
        int position = scanner.nextInt();
        scanner.nextLine();

        if (position < 1 || position > targetQueue.size()) {
            System.out.println("Invalid position.");
            return;
        }

            Customer removedCustomer = targetQueue.remove(position - 1);
            if (removedCustomer != null) {
                System.out.println("Customer " + removedCustomer.getFullName() + " removed from queue " + queueNumber);

                if (!waitingQueue.isEmpty()) {
                    Customer customerFromWaitingList = waitingQueue.remove(0);
                    targetQueue.add(customerFromWaitingList);
                    System.out.println("Customer " + customerFromWaitingList.getFullName() + " added to Queue " + queueNumber + " from the waiting list.");
                }
            }
        }





        public static void removeServedCustomer() {
        //Remove a served customer from a queue
        System.out.println("Enter the queue number (1-3):");
        int queueNumber = scanner.nextInt();
        scanner.nextLine();

        if (queueNumber < 1 || queueNumber > 3) {
            System.out.println("Invalid queue number.");
            return;
        }

        List<Customer> targetQueue = null;
        switch (queueNumber) {
            case 1:
                targetQueue = GUI.Queue_1;
                break;
            case 2:
                targetQueue = GUI.Queue_2;
                break;
            case 3:
                targetQueue = GUI.Queue_3;
                break;
        }

        if (targetQueue == null || targetQueue.isEmpty()) {
            System.out.println("Queue " + queueNumber + " is empty.");
            return;
        }

            Customer servedCustomer = targetQueue.remove(0);
            if (servedCustomer != null) {
                System.out.println("Served customer " + servedCustomer.getFullName() + " removed from queue " + queueNumber);

                if (!waitingQueue.isEmpty()) {
                    Customer customerFromWaitingList = waitingQueue.remove(0);
                    targetQueue.add(customerFromWaitingList);
                    System.out.println("Customer " + customerFromWaitingList.getFullName() + " added to Queue " + queueNumber + " from the waiting list.");
                }
            }
        }


    public static FoodQueue findQueueWithMinimumLength(List<FoodQueue> queues) {
        //Find the queue with the minimum length
        FoodQueue minQueue = null;
        int minLength = Integer.MAX_VALUE;

        for (FoodQueue queue : queues) {
            int queueLength = queue.getSize();
            if (queueLength < minLength) {
                minLength = queueLength;
                minQueue = queue;
            }
        }

        return minQueue;
    }

    public static void refreshQueues() {
        //Refresh the display of food queues and waiting queue
        StringBuilder foodQueueBuilder = new StringBuilder();

        foodQueueBuilder.append("Queue 1:\n");
        for (Customer customer : GUI.Queue_1) {
            foodQueueBuilder.append(customer.getFullName()).append("\n");
        }
        foodQueueBuilder.append("Queue 2:\n");
        for (Customer customer : GUI.Queue_2) {
            foodQueueBuilder.append(customer.getFullName()).append("\n");
        }
        foodQueueBuilder.append("Queue 3:\n");
        for (Customer customer : GUI.Queue_3) {
            foodQueueBuilder.append(customer.getFullName()).append("\n");
        }
        GUI.foodQueueTextArea.setText(foodQueueBuilder.toString());

        StringBuilder waitingQueueBuilder = new StringBuilder();
        for (Customer customer : waitingQueue) {
            waitingQueueBuilder.append(customer.getFullName()).append("\n");
        }
        GUI.waitingQueueTextArea.setText(waitingQueueBuilder.toString());
    }



    public static void searchingTheCustomer() {
        //Search for a customer in the queues
        String searchName = GUI.firstNameField.getText().trim();
        StringBuilder output = new StringBuilder();

        for (Customer customer : GUI.Queue_1) {
            if (customer.getFirstName().equalsIgnoreCase(searchName)) {
                output.append("Queue no. 1:\n");
                output.append(customer.getFullDetails()).append("\n");
            }
        }
        for (Customer customer : GUI.Queue_2) {
            if (customer.getFirstName().equalsIgnoreCase(searchName)) {
                output.append("Queue no. 2:\n");
                output.append(customer.getFullDetails()).append("\n");
            }
        }
        for (Customer customer : GUI.Queue_3) {
            if (customer.getFirstName().equalsIgnoreCase(searchName)) {
                output.append("Queue no. 3:\n");
                output.append(customer.getFullDetails()).append("\n");
            }
        }
        for (Customer customer : waitingQueue) {
            if (customer.getFirstName().equalsIgnoreCase(searchName)) {
                output.append("Waiting Queue:\n");
                output.append(customer.getFullDetails()).append("\n");
            }
        }

        if (output.length() == 0) {
            output.append("Error. Customer not found.");
        }

        GUI.searchResults.setText(output.toString());
    }
}

class Customer {
    private String firstName;
    private String lastName;
    private int burgersRequired;

    public Customer(String firstName, String lastName, int burgersRequired) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.burgersRequired = burgersRequired;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBurgersRequired() {
        return burgersRequired;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFullDetails() {
        return firstName + " " + lastName + " " + burgersRequired;
    }

    public int compareTo(Customer other) {
        return this.getFullName().compareTo(other.getFullName());
    }
}
