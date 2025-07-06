package ma.enset.ebankingbackend.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.enums.OperationType;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficentException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.mappers.BankAccountMapperImpl;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving a New customer");// message log
        Customer customer  = bankAccountMapper.customerDTOToCustomer(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.customerToCustomerDTO(savedCustomer);
    }

    // get full customer
    @Override
    public Customer getCustomerById(Long customerId) throws CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->  new CustomerNotFoundException("Customer not found"));
        return customer;
    }

    // getcustomerDto
    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = getCustomerById(customerId);
        return bankAccountMapper.customerToCustomerDTO(customer);
    }

    @Override
    public BankCurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        CurrentAccount currentAccount = new CurrentAccount();

        Customer customer = getCustomerById(customerId);

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreationDate(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);

       return bankAccountMapper.currentAccountToCurrentAccountDTO(savedCurrentAccount);
    }

    @Override
    public BankSavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {

        SavingAccount savingAccount = new SavingAccount();
        Customer customer =  getCustomerById(customerId);

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreationDate(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedAccount = bankAccountRepository.save(savingAccount);
        return bankAccountMapper.savingAccountToSavingAccountDTO(savedAccount);

    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> bankAccountMapper.customerToCustomerDTO(customer)).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccountDTO(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));
        if(bankAccount  instanceof SavingAccount savingAccount) {
            return bankAccountMapper.savingAccountToSavingAccountDTO(savingAccount);
        }
        else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.currentAccountToCurrentAccountDTO(currentAccount);
        }

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

    private BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));
        return bankAccount;
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {

            BankAccount bankAccount = getBankAccount(accountId);

            if(bankAccount.getBalance() < amount ) {
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
    public List<BankAccountDTO> bankAccountList() {

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount savingAccount) {
                return bankAccountMapper.savingAccountToSavingAccountDTO(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankAccountMapper.currentAccountToCurrentAccountDTO(currentAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving a New customer");// message log
        Customer customer  = bankAccountMapper.customerDTOToCustomer(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.customerToCustomerDTO(savedCustomer);
    }

    @Override
    public void deleteCustomerById(Long id)  {
        //CustomerDTO customer = getCustomer(id);
        customerRepository.deleteById(id);
    }

    @Override
    public List<BankAccountOperationDTO> accountHistory(String accountId) {
        
        List<BankAccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op -> bankAccountMapper.bankAccountOperationToBankAccountOperationDTO(op))
                .collect(Collectors.toList());

    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = getBankAccount(accountId);
        Page<BankAccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setBankAccountOperationDTOS(accountOperations.getContent().stream().map(op -> {
            return bankAccountMapper.bankAccountOperationToBankAccountOperationDTO(op);
        }).collect(Collectors.toList()));
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        accountHistoryDTO.setCurrentPage(page);
        return accountHistoryDTO;

    }
}
