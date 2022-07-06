import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Customer {
        private String fullName;
        private String cpf;
        private LocalDate birthDate;
        private List<Telephone> telephones;
        private List<Address> addresses;
        private String email;
        private boolean isActivated;

        public Customer(String fullName, String cpf, LocalDate birthDate, List<Telephone> telephones, List<Address> addresses, String email) {
            this.fullName = fullName;
            this.cpf = cpf;
            this.birthDate = birthDate;
            this.telephones = telephones;
            this.addresses = addresses;
            this.email = email;
            this.isActivated = true;
        }

    public Customer() {

    }

    public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public List<Telephone> getTelephones() {
            return telephones;
        }

        public void setTelephones(List<Telephone> telephones) {
            this.telephones = telephones;
        }

        public List<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<Address> addresses) {
            this.addresses = addresses;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isActivated() {
            return isActivated;
        }

        public void setActivated(boolean activated) {
            isActivated = activated;
        }



        @Override
        public String toString() {
            return "Customer{" +
                    "fullName='" + fullName + '\'' +
                    ", CPF='" + cpf + '\'' +
                    ", birthDate=" + birthDate +
                    ", telephone=" + telephones +
                    ", address=" + addresses +
                    ", email='" + email + '\'' +
                    ", ativado='" + isActivated + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Customer customer = (Customer) o;
            return isActivated == customer.isActivated && Objects.equals(fullName, customer.fullName) && Objects.equals(cpf, customer.cpf) && Objects.equals(birthDate, customer.birthDate) && Objects.equals(telephones, customer.telephones) && Objects.equals(addresses, customer.addresses) && Objects.equals(email, customer.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fullName, cpf, birthDate, telephones, addresses, email, isActivated);
        }
}
