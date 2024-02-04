import java.util.*;

public class FoodQueue {
    private int capacity;
    private Queue<Customer> customers;
    private int size;

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

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public static void displayQueueVisual(List<FoodQueue> queues) {
        //Display the visual representation of the queues
        System.out.println("*****************");
        System.out.println("* Cashiers *");
        System.out.println("*****************");

        int maxQueueCapacity = getMaxQueueCapacity(queues);

        for (int j = 0; j < maxQueueCapacity; j++) {
            for (int i = 0; i < Main.MAX_CASHIERS; i++) {
                if (j < Main.QUEUE_CAPACITY[i]) {
                    FoodQueue queue = queues.get(i);
                    if (j < queue.customers.size()) {
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

    private static int getMaxQueueCapacity(List<FoodQueue> queues) {
        //Find the maximum queue capacity among all queues
        int maxCapacity = 0;
        for (FoodQueue queue : queues) {
            if (queue.getCapacity() > maxCapacity) {
                maxCapacity = queue.getCapacity();
            }
        }
        return maxCapacity;
    }

    private int getCapacity() {
        return capacity;
    }

    public static FoodQueue findQueueWithMinimumLength(List<FoodQueue> queues) {
        //Find the queues with the minimum length
        FoodQueue minQueue = null;
        int minLength = Integer.MAX_VALUE;

        for (FoodQueue queue : queues) {
            int queueLength = queue.customers.size();
            if (queueLength < minLength) {
                minLength = queueLength;
                minQueue = queue;
            }
        }

        return minQueue;
    }

    public int getQueueLength() {
        //Return the length of the queue
        return 0;
    }
}
