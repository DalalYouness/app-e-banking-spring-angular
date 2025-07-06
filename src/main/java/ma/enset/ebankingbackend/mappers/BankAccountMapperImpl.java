package ma.enset.ebankingbackend.mappers;

import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    public CustomerDTO customerToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        // c'est mieux d'utiliser la class BeansUtils : premier solution y'as aussi des framework qui font ca comme mappstruct
        BeanUtils.copyProperties(customer, customerDTO);
//      customerDTO.setId(customer.getId());
//      customerDTO.setName(customer.getName());
//      customerDTO.setEmail(customer.getEmail());
        return customerDTO;
    }

    public Customer customerDTOToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    public BankCurrentAccountDTO currentAccountToCurrentAccountDTO(CurrentAccount currentAccount) {
        BankCurrentAccountDTO currentAccountDTO = new BankCurrentAccountDTO();
        BeanUtils.copyProperties(currentAccount,currentAccountDTO);
        currentAccountDTO.setCustomerDTO(customerToCustomerDTO(currentAccount.getCustomer()));
        currentAccountDTO.setType(currentAccount.getClass().getSimpleName());
        return currentAccountDTO;
    }

    public CurrentAccount currentAccountDTOToCurrentAccount(BankCurrentAccountDTO bankCurrentAccountDTO) {
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(bankCurrentAccountDTO,currentAccount);
        currentAccount.setCustomer(customerDTOToCustomer(bankCurrentAccountDTO.getCustomerDTO()));
        return currentAccount;
    }

    public BankSavingAccountDTO savingAccountToSavingAccountDTO (SavingAccount savingAccount) {
        BankSavingAccountDTO savingAccountDTO = new BankSavingAccountDTO();
        BeanUtils.copyProperties(savingAccount,savingAccountDTO);
        savingAccountDTO.setCustomerDTO(customerToCustomerDTO(savingAccount.getCustomer()));
        savingAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingAccountDTO;
    }

    public SavingAccount savingAccountDTOToSavingAccount(BankSavingAccountDTO savingAccountDTO) {
        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingAccountDTO,savingAccount);
        savingAccount.setCustomer(customerDTOToCustomer(savingAccountDTO.getCustomerDTO()));
        return savingAccount;
    }

    public BankAccountOperationDTO bankAccountOperationToBankAccountOperationDTO(BankAccountOperation bankAccountOperation) {
        BankAccountOperationDTO bankAccountOperationDTO = new BankAccountOperationDTO();
        BeanUtils.copyProperties(bankAccountOperation,bankAccountOperationDTO);
        return bankAccountOperationDTO;
    }
}
