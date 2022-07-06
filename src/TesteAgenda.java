import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TesteAgenda {
    public static void main(String[] args) throws AlreadyExistsException, InvalidFormatException, NotFoundException {

        String driver = "postgresql";
        String dataBaseAddress = "localhost:5432";
        String dataBaseName = "salao";
        String user = "postgres";
        String password = "postgres";

        StringBuilder sb = new StringBuilder("jdbc:")
                .append(driver).append("://")
                .append(dataBaseAddress).append("/")
                .append(dataBaseName);

        String connectionUrl = sb.toString();

        try(Connection conn = DriverManager.getConnection(connectionUrl, user, password)){
            System.out.println("conectado com sucesso no banco postgresSQL");

            CustomersService customersService = new CustomersService(conn);
            boolean logado = true;
            while(logado){

                Object[] options =  {"Detalhes do cliente", "Adicionar cliente", "Editar cliente", "Remover cliente", "Sair"};
                int selectedOption = JOptionPane.showOptionDialog(null,
                        "Escolha alguma das opções abaixo:",
                        "Gestão de clientes",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]);

                switch (selectedOption){

                    case 0:
                        try {
                            String inputValue = JOptionPane.showInputDialog("Insira o CPF do cliente");
                            System.out.println(inputValue);

                            Customer customerFound = customersService.getCustomer(inputValue);
                            System.out.println(customerFound);

                            JOptionPane.showMessageDialog(null,
                                    "Nome: " + customerFound.getFullName() +
                                            "\n" + "CPF: " + customerFound.getCpf() +
                                            "\n" + "Data nascimento: " + customerFound.getBirthDate() +
                                            "\n" + "Telefone: " +  customerFound.getTelephones().get(0).getDdd() +
                                            " " + customerFound.getTelephones().get(0).getPhoneNumber() +
                                            "\n" + "Estado: " + customerFound.getAddresses().get(0).getState() +
                                            "\n" + "Cidade: " + customerFound.getAddresses().get(0).getCity() +
                                            "\n" + "CEP: " + customerFound.getAddresses().get(0).getZipCode() +
                                            "\n" + "Rua:  " + customerFound.getAddresses().get(0).getStreet() +
                                            "\n" + "Referência: " + customerFound.getAddresses().get(0).getReference() +
                                            "\n" + "E-mail: " + customerFound.getEmail());

                        } catch (NotFoundException e){
                            System.out.println("Usuário não encontrado!");
                            JOptionPane.showMessageDialog(null, "Usuário não encontrado");
                        }

                        break;

                    case 1:

                        try{
                            JTextField nameField = new JTextField(20);
                            JTextField cpfField = new JTextField(20);
                            JTextField birthDateField = new JTextField(20);
                            JTextField dddField = new JTextField(20);
                            JTextField telephonesField = new JTextField(20);
                            JTextField stateField = new JTextField(20);
                            JTextField cityField = new JTextField(20);
                            JTextField streetField = new JTextField(20);
                            JTextField zipCodeField = new JTextField(20);
                            JTextField referenceField = new JTextField(20);
                            JTextField emailField = new JTextField(20);

                            JPanel myPanel = new JPanel();
                            myPanel.setLayout(new GridLayout(0,2));
                            myPanel.add(new JLabel("Nome completo:"));
                            myPanel.add(nameField);
                            myPanel.add(new JLabel("CPF:"));
                            myPanel.add(cpfField);
                            myPanel.add(new JLabel("Data de nascimento (xx/xx/xxxx):"));
                            myPanel.add(birthDateField);
                            myPanel.add(new JLabel("DDD:"));
                            myPanel.add(dddField);
                            myPanel.add(new JLabel("Telefone:"));
                            myPanel.add(telephonesField);
                            myPanel.add(new JLabel("Estado:"));
                            myPanel.add(stateField);
                            myPanel.add(new JLabel("Cidade:"));
                            myPanel.add(cityField);
                            myPanel.add(new JLabel("Rua:"));
                            myPanel.add(streetField);
                            myPanel.add(new JLabel("CEP:"));
                            myPanel.add(zipCodeField);
                            myPanel.add(new JLabel("Referência:"));
                            myPanel.add(referenceField);
                            myPanel.add(new JLabel("E-mail:"));
                            myPanel.add(emailField);

                            int result = JOptionPane.showConfirmDialog(null, myPanel,
                                    "Por favor, insira os dados: ", JOptionPane.OK_CANCEL_OPTION);
                            if (result == JOptionPane.OK_OPTION) {
                                System.out.println("Nome completo: " + nameField.getText());
                                System.out.println("CPF: " + cpfField.getText());
                                System.out.println("Data de nascimento: " + birthDateField.getText());
                                System.out.println("DDD: " + dddField.getText());
                                System.out.println("Telefone: " + telephonesField.getText());
                                System.out.println("Estado: " + stateField.getText());
                                System.out.println("Cidade: " + cityField.getText());
                                System.out.println("Rua: " + streetField.getText());
                                System.out.println("CEP: " + zipCodeField.getText());
                                System.out.println("Referência: " + referenceField.getText());
                                System.out.println("E-mail: " + emailField.getText());
                            }

                            Customer customer = new Customer(
                                    nameField.getText(),
                                    cpfField.getText(),
                                    LocalDate.parse(birthDateField.getText(), DateTimeFormatter.ofPattern("d/MM/yyyy")),
                                    List.of( new Telephone(dddField.getText(), telephonesField.getText())),
                                    List.of( new Address(stateField.getText(), cityField.getText(), streetField.getText(), zipCodeField.getText(), referenceField.getText())),
                                    emailField.getText()
                            );

                            customersService.addCustomer(customer);

                            JOptionPane.showMessageDialog(null,
                                    "Nome: " + nameField.getText() +
                                            "\n" + "CPF: " + cpfField.getText() +
                                            "\n" + "Data nascimento: " + birthDateField.getText() +
                                            "\n" + "Telefone: " + dddField.getText() + " " + telephonesField.getText() +
                                            "\n" + "Endereço: " + stateField.getText() + " " + cityField.getText() + " " +
                                            streetField.getText() + " " + zipCodeField.getText() + " " + referenceField.getText() +
                                            "\n" + "E-mail: " + emailField.getText());

                            JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!");
                        } catch (InvalidFormatException e){
                            System.out.println("Usuário não cadastrado. CPF inválido.");
                        } catch (AlreadyExistsException e){
                            System.out.println("Usuário não cadastrado. CPF já existe.");
                        }

                        break;

                    case 2:
                        try{
                            String inputValue = JOptionPane.showInputDialog("Insira o CPF do cliente: ");
                            Customer customerFound = customersService.getCustomer(inputValue);

                            // Criar campos
                            JTextField nameFieldEdit = new JTextField(20);
                            JTextField birthDateFieldEdit = new JTextField(20);
                            JTextField dddFieldEdit = new JTextField(20);
                            JTextField telephonesFieldEdit = new JTextField(20);
                            JTextField stateFieldEdit = new JTextField(20);
                            JTextField cityFieldEdit = new JTextField(20);
                            JTextField streetFieldEdit = new JTextField(20);
                            JTextField zipCodeFieldEdit = new JTextField(20);
                            JTextField referenceFieldEdit = new JTextField(20);
                            JTextField emailFieldEdit = new JTextField(20);

                            // Adicionar valores do usuario encontrado nos campos
                            nameFieldEdit.setText(customerFound.getFullName());
                            birthDateFieldEdit.setText(customerFound.getBirthDate().format(DateTimeFormatter.ofPattern("d/MM/yyyy")));
                            dddFieldEdit.setText(customerFound.getTelephones().get(0).getDdd());
                            telephonesFieldEdit.setText(customerFound.getTelephones().get(0).getPhoneNumber());
                            stateFieldEdit.setText(customerFound.getAddresses().get(0).getState());
                            cityFieldEdit.setText(customerFound.getAddresses().get(0).getCity());
                            streetFieldEdit.setText(customerFound.getAddresses().get(0).getStreet());
                            zipCodeFieldEdit.setText(customerFound.getAddresses().get(0).getZipCode());
                            referenceFieldEdit.setText(customerFound.getAddresses().get(0).getReference());
                            emailFieldEdit.setText(customerFound.getEmail());

                            // Criacao do painel com os campos criados anteriormente
                            JPanel myPanelEdit = new JPanel();
                            myPanelEdit.setLayout(new GridLayout(0,2));
                            myPanelEdit.add(new JLabel("Nome completo:"));
                            myPanelEdit.add(nameFieldEdit);
                            myPanelEdit.add(new JLabel("Data de nascimento (xx/xx/xxxx):"));
                            myPanelEdit.add(birthDateFieldEdit);
                            myPanelEdit.add(new JLabel("DDD:"));
                            myPanelEdit.add(dddFieldEdit);
                            myPanelEdit.add(new JLabel("Telefone:"));
                            myPanelEdit.add(telephonesFieldEdit);
                            myPanelEdit.add(new JLabel("Estado:"));
                            myPanelEdit.add(stateFieldEdit);
                            myPanelEdit.add(new JLabel("Cidade:"));
                            myPanelEdit.add(cityFieldEdit);
                            myPanelEdit.add(new JLabel("Rua:"));
                            myPanelEdit.add(streetFieldEdit);
                            myPanelEdit.add(new JLabel("CEP:"));
                            myPanelEdit.add(zipCodeFieldEdit);
                            myPanelEdit.add(new JLabel("Referência:"));
                            myPanelEdit.add(referenceFieldEdit);
                            myPanelEdit.add(new JLabel("E-mail:"));
                            myPanelEdit.add(emailFieldEdit);

                            // Exibir painel para o usuario
                            int resultEdit = JOptionPane.showConfirmDialog(null, myPanelEdit,
                                    "Por favor, insira os dados que deseja editar: ", JOptionPane.OK_CANCEL_OPTION);

                            // Caso aperter ok
                            if (resultEdit == JOptionPane.OK_OPTION) {
                                Customer customerWithValuesEdited = new Customer(
                                        nameFieldEdit.getText(),
                                        null,
                                        LocalDate.parse(birthDateFieldEdit.getText(), DateTimeFormatter.ofPattern("d/MM/yyyy")),
                                        List.of( new Telephone(dddFieldEdit.getText(), telephonesFieldEdit.getText())),
                                        List.of( new Address(stateFieldEdit.getText(), cityFieldEdit.getText(), streetFieldEdit.getText(),
                                                zipCodeFieldEdit.getText(), referenceFieldEdit.getText())),
                                        emailFieldEdit.getText()
                                );

                                Customer customerEdited = customersService.editCustomer(customerFound.getCpf(), customerWithValuesEdited);

                                JOptionPane.showMessageDialog(null,
                                        "Nome: " + nameFieldEdit.getText() +
                                                "\n" + "Data nascimento: " + birthDateFieldEdit.getText() +
                                                "\n" + "Telefone: " + dddFieldEdit.getText() + " " + telephonesFieldEdit.getText() +
                                                "\n" + "Endereço: " + stateFieldEdit.getText() + " " + cityFieldEdit.getText() + " " +
                                                streetFieldEdit.getText() + " " + zipCodeFieldEdit.getText() + " " + referenceFieldEdit.getText() +
                                                "\n" + "E-mail: " + emailFieldEdit.getText());

                                JOptionPane.showMessageDialog(null, "Usuário editado com sucesso!");
                            }


                        } catch (InvalidFormatException e) {
                            System.out.println("O campo editado não pode ser salvo. Formato usado não é permitido.");
                        }

                        break;

                    case 3:
                        try {
                            String inputValueRemove = JOptionPane.showInputDialog("Insira o CPF do cliente");

                            customersService.removeCustomer(inputValueRemove);

                            JOptionPane.showMessageDialog(null, "Usuário removido com sucesso!");
                        } catch (NotFoundException e){
                            System.out.println("Usuário não encontrado!");
                        }

                        break;

                    case 4:
                        logado = false;
                        System.out.println("Sair");
                        break;

                    default:
                        System.out.println("Nenhum dos cases foi aceito");
                }
            }

        } catch (SQLException e){
            System.out.println("falha ao se conectar ao banco postgresSQL");
            e.printStackTrace();
        }





    }
}
