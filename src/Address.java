public class Address {
    private String state;
    private String city;
    private String street;
    private String zipCode;
    private String reference;

    public Address(String state, String city, String street, String zipCode, String reference) {
        this.state = state;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
        this.reference = reference;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Address{" +
                "state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }
}
