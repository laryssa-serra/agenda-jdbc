public class Telephone {
    private String ddd;
    private String phoneNumber;

    public Telephone(String ddd, String phoneNumber) {
        this.ddd = ddd;
        this.phoneNumber = phoneNumber;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Telephone{" +
                ", ddd='" + ddd + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
