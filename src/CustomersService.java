import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomersService {
    private Connection conn = null;
    private List<Customer> customerList = new ArrayList<>();

    public CustomersService(Connection conn) {
        this.conn = conn;
    }

    public List<Customer> listCustomers() throws SQLException {
        String sql = "SELECT * FROM clients";

        PreparedStatement stmt = conn.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        System.out.println(rs);

        return null;
    }
    
    public Customer getCustomer(String cpf) throws NotFoundException, SQLException {

        String sql = "SELECT * FROM clients where cpf = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cpf);

        ResultSet rs = stmt.executeQuery();
        if (!rs.isBeforeFirst() ) {
            throw new NotFoundException("Usuário não encontrado a partir do CPF informado.");
        }

        Customer customerFound = new Customer();
        if(rs.next()){
            customerFound =  new Customer(
                    rs.getString("fullName"),
                    rs.getString("cpf"),
                    rs.getDate("birthDate").toLocalDate(),
                    null,
                    null,
                    rs.getString("email")
            );
        }

        String sqlTelephone = "select t.* " +
                "from clients c " +
                "inner join telephones t on t.id_client = c.id_client " +
                "where c.cpf = ?";

        PreparedStatement statement = conn.prepareStatement(sqlTelephone);
        statement.setString(1, cpf);
        ResultSet result = statement.executeQuery();

        List telephonesList = new LinkedList();

        while(result.next()){
            String str = result.getString("ddd");
            String str2 = result.getString("phoneNumber");

            Telephone telephone = new Telephone(str, str2);

            telephonesList.add(telephone);
        }

        customerFound.setTelephones(telephonesList);

        String sqlAddress = "select a.* " +
                "from clients c " +
                "inner join address a on a.id_client = c.id_client " +
                "where c.cpf = ?";

        PreparedStatement statementAddress = conn.prepareStatement(sqlAddress);
        statementAddress.setString(1, cpf);
        ResultSet resultAddress = statementAddress.executeQuery();

        List addressList = new LinkedList();

        while(resultAddress.next()){
            String strState = resultAddress.getString("state");
            String strCity = resultAddress.getString("city");
            String strStreet = resultAddress.getString("street");
            String strZipCode = resultAddress.getString("zipCode");
            String strReference = resultAddress.getString("reference");

            Address address = new Address(strState, strCity, strStreet, strZipCode, strReference);

            addressList.add(address);
        }

        customerFound.setAddresses(addressList);

        return customerFound;
    }
    
    public Customer addCustomer(Customer customer) throws InvalidFormatException, AlreadyExistsException, SQLException {


        if(!isCpfValid(customer.getCpf())){

            throw new InvalidFormatException("Usuário não cadastrado. CPF inválido.");
        }

        if(!isEmailValid(customer.getEmail())){
            throw new InvalidFormatException("Usuário não cadastrado. E-mail inserido não é válido");
        }

        if(!areTelephonesValids(customer.getTelephones())){
            throw new InvalidFormatException("O número de telefone inserido está incorreto.");
        }

        String sql = "INSERT INTO clients(cpf, fullName, birthDate, email, isactivated) VALUES(?, ?, ?, ?, true)";

        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, customer.getCpf());
        stmt.setString(2, customer.getFullName());
        stmt.setDate(3, java.sql.Date.valueOf(customer.getBirthDate()));
        stmt.setString(4, customer.getEmail());

        int affectedRows = stmt.executeUpdate();

        Long id = 0L;

        if (affectedRows > 0) {
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        for(Telephone  telephone : customer.getTelephones()){
            String sqlTelephone = "INSERT INTO telephones (id_client, phoneNumber, ddd) VALUES(?, ?, ?)";

            PreparedStatement stmtTelephone = conn.prepareStatement(sqlTelephone);

            stmtTelephone.setLong(1, id);
            stmtTelephone.setString(2, telephone.getPhoneNumber());
            stmtTelephone.setString(3, telephone.getDdd());

            stmtTelephone.executeUpdate();
        }

        for(Address address : customer.getAddresses()){
            String sqlAddress = "INSERT INTO address(id_client, state, city, street, zipCode, reference) VALUES(?, ?, ?, ?, ?, ?)";

            PreparedStatement stmtAddress = conn.prepareStatement(sqlAddress);

            stmtAddress.setLong(1, id);
            stmtAddress.setString(2, address.getState());
            stmtAddress.setString(3, address.getCity());
            stmtAddress.setString(4, address.getStreet());
            stmtAddress.setString(5, address.getZipCode());
            stmtAddress.setString(6, address.getReference());

            stmtAddress.executeUpdate();
        }

        System.out.println("Usuário cadastrado com sucesso!");

        return customer;
    }

    public Customer editCustomer(String customerToChangeCpf, Customer customerChanges) throws NotFoundException, InvalidFormatException, SQLException {

        if(!isEmailValid(customerChanges.getEmail()))
            try {
                throw new InvalidFormatException("E-mail não pode ser salvo. E-mail inserido não é válido.");
            } catch (InvalidFormatException e) {
                throw new RuntimeException(e);
            }

        if(!areTelephonesValids(customerChanges.getTelephones()))
            try {
                throw new InvalidFormatException("O número de telefone inserido está incorreto.");
            } catch (InvalidFormatException e) {
                throw new RuntimeException(e);
            }

        String sql = "UPDATE clients " +
                "SET fullName = ?, birthDate = ?, email = ? " +
                "WHERE cpf = ? ";

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, customerChanges.getFullName());
        stmt.setDate(2, java.sql.Date.valueOf(customerChanges.getBirthDate()));
        stmt.setString(3, customerChanges.getEmail());

        stmt.setString(4, customerToChangeCpf);

        int affectedRows = stmt.executeUpdate();


        String sqlGetId = "SELECT id_client FROM clients where cpf = ? ";
        PreparedStatement stmtGetId = conn.prepareStatement(sqlGetId);
        stmtGetId.setString(1, customerToChangeCpf);
        ResultSet rsGetId = stmtGetId.executeQuery();

        Long id = 0L;
        while(rsGetId.next()){
            id = rsGetId.getLong("id_client");
        }


        for(Telephone  telephone : customerChanges.getTelephones()){

            String sqlTelephone = "UPDATE telephones set phoneNumber = ?, ddd = ?  WHERE id_client = ?";

            PreparedStatement stmtTelephone = conn.prepareStatement(sqlTelephone);

            stmtTelephone.setString(1, telephone.getPhoneNumber());
            stmtTelephone.setString(2, telephone.getDdd());
            stmtTelephone.setLong(3, id);

            stmtTelephone.executeUpdate();
        }

        for(Address address : customerChanges.getAddresses()){
            String sqlAddress = "UPDATE address SET state = ?, city = ?, street = ?, zipCode = ?, reference = ? WHERE id_client = ?";

            PreparedStatement stmtAddress = conn.prepareStatement(sqlAddress);

            stmtAddress.setString(1, address.getState());
            stmtAddress.setString(2, address.getCity());
            stmtAddress.setString(3, address.getStreet());
            stmtAddress.setString(4, address.getZipCode());
            stmtAddress.setString(5, address.getReference());
            stmtAddress.setLong(6, id);

            stmtAddress.executeUpdate();
        }

        return customerChanges;
    }

    public void removeCustomer(String cpf) throws NotFoundException, SQLException {
        String sql = "UPDATE clients " +
                "SET isactivated = 'false' " + " " +
                "WHERE cpf = ? ";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cpf);

        stmt.executeUpdate();

        System.out.println("Usuário " + cpf + " deletado com sucesso!");
    }

    public boolean areTelephonesValids (List<Telephone> telephones) {
        for(Telephone tel :  telephones){
            String completePhoneNumber = tel.getDdd().concat(tel.getPhoneNumber());
            if(!isTelephoneValid(completePhoneNumber)){
                return false;
            }
        }
        return true;
    }

    private boolean checkCpfExist(String cpf) {
        return customerList
                .stream()
                .anyMatch(customer -> customer.getCpf().equals(cpf));
    }

    private boolean isEmailValid(String email){
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

     private boolean isTelephoneValid(String telephone){
        telephone = telephone.replaceAll("\\D", "");

        if(!(telephone.length() >= 10 && telephone.length() <= 11)){
            return false;
        }

        if(telephone.length() == 11 && Integer.parseInt(telephone.substring(2,3)) != 9){
            return false;
        }

        Pattern pattern = java.util.regex.Pattern.compile(telephone.charAt(0)+"{"+telephone.length()+"}");
        Matcher matcher = pattern.matcher(telephone);
        if(matcher.find()){
            return false;
        }

        Integer[] codigosDDD = {
                11, 12, 13, 14, 15, 16, 17, 18, 19,
                21, 22, 24, 27, 28, 31, 32, 33, 34,
                35, 37, 38, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 51, 53, 54, 55, 61, 62,
                64, 63, 65, 66, 67, 68, 69, 71, 73,
                74, 75, 77, 79, 81, 82, 83, 84, 85,
                86, 87, 88, 89, 91, 92, 93, 94, 95,
                96, 97, 98, 99};
        if(java.util.Arrays.asList(codigosDDD).indexOf(Integer.parseInt(telephone.substring(0,2))) == -1){
            return false;
        }

        Integer[] prefixo = {2, 3, 4, 5, 7};
        if(telephone.length() == 10 && java.util.Arrays.asList(prefixo).indexOf(Integer.parseInt(telephone.substring(2,3))) == -1){
            return false;
        }

        return true;
    }

    private boolean isCpfValid(String cpf){

        return cpf.matches("([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})");
    }
}
