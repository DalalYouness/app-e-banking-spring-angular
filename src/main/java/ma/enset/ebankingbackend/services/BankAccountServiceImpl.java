package ma.enset.ebankingbackend.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.enums.OperationType;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficentException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.mappers.BankAccountMapperImpl;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor //Dependency Injection Using Lombok
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {


    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapperImpl bankAccountMapper;

    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("Saving a New customer");// message log
        Customer Addedcustomer = customerRepository.save(customer);
        return Addedcustomer;
    }

    @Override
    public Customer getCustomerById(Long customerId) throws CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->  new CustomerNotFoundException("Customer not found"));
        return customer;
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        CurrentAccount currentAccount = new CurrentAccount();

        Customer customer = getCustomerById(customerId);

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreationDate(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        return  bankAccountRepository.save(currentAccount);

    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {

        SavingAccount savingAccount = new SavingAccount();
        Customer customer =  getCustomerById(customerId);

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreationDate(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        return bankAccountRepository.save(savingAccount);

    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> bankAccountMapper.customerToCustomerDTO(customer)).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));
        return bankAccount;
    }

    private void createOperation(OperationType Type,double amount,String description , BankAccount bankAccount, Date operationDate) {
        BankAccountOperation operation = new BankAccountOperation();
        operation.setOperationDate(operationDate);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setBankAccount(bankAccount);
        operation.setType(Type);
        accountOperationRepository.save(operation);
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {

            BankAccount bankAccount = getBankAccount(accountId);

            if(bankAccount.getBalance() < amount) {
                throw new BalanceNotSufficentException("Balance Not Sufficient");
            }

            createOperation(OperationType.DEBIT,amount,description,bankAccount,new Date());
            bankAccount.setBalance(bankAccount.getBalance() - amount);
            bankAccountRepository.save(bankAccount);


    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {

        BankAccount bankAccount = getBankAccount(accountId);
        createOperation(OperationType.CREDIT,amount,description,bankAccount,new Date());
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);


    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException {

        debit(accountIdSource,amount,"Transfert To " + accountIdDestination);
        credit(accountIdDestination,amount,"Transfert From " + accountIdSource);

    }

    @Override
    public List<BankAccount> bankAccountList() {
        return bankAccountRepository.findAll();
    }

}
