package ma.enset.ebankingbackend.services;

import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.entities.BankAccount;
import ma.enset.ebankingbackend.entities.CurrentAccount;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.entities.SavingAccount;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficentException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    // getcustomerDto
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
    BankCurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft , Long CustomerId) throws CustomerNotFoundException;
    BankSavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate , Long CustomerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccountDTO(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount,String description) throws BankAccountNotFoundException, BalanceNotSufficentException;
    void credit(String accountId, double amount,String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource , String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException;
    Customer getCustomerById(Long customerId) throws CustomerNotFoundException;
    List<BankAccountDTO> bankAccountList();
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    void deleteCustomerById(Long id);
    List<BankAccountOperationDTO> accountHistory(String accountId);


    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
