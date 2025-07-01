package ma.enset.ebankingbackend;

import jdk.swing.interop.SwingInterOpUtils;
import ma.enset.ebankingbackend.entities.BankAccountOperation;
import ma.enset.ebankingbackend.entities.CurrentAccount;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.entities.SavingAccount;
import ma.enset.ebankingbackend.enums.AccountStatus;
import ma.enset.ebankingbackend.enums.OperationType;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Currency;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;
import static java.lang.Math.*;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {
        return args -> {
            Stream.of("Youness","Ahmed","Simo").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setCustomer(customer);
                currentAccount.setBalance(Math.random() * 80000);
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCreationDate(new Date());
                currentAccount.setOverDraft(1400);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setCustomer(customer);
                savingAccount.setBalance(random() * 80000);
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCreationDate(new Date());
                savingAccount.setInterestRate(1400);
                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for (int i = 0; i < 10; i++) {
                    BankAccountOperation bankAccountOp = new BankAccountOperation();
                    bankAccountOp.setBankAccount(bankAccount);
                    bankAccountOp.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    bankAccountOp.setAmount(random() * 6000 * i);
                    bankAccountOp.setOperationDate(new Date());
                    accountOperationRepository.save(bankAccountOp);
                }

            });

        };
    }

}
