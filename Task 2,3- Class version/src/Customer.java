public class Customer {
    String firstName;
    String lastName;
    int burgersRequired;

    public Customer(String firstName, String lastName, int burgersRequired) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.burgersRequired = burgersRequired;
    }

    public Customer(){

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

