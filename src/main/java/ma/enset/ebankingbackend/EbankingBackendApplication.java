package ma.enset.ebankingbackend;

import ma.enset.ebankingbackend.dtos.BankAccountDTO;
import ma.enset.ebankingbackend.dtos.BankCurrentAccountDTO;
import ma.enset.ebankingbackend.dtos.BankSavingAccountDTO;
import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficentException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    //pour tester
    @Bean
    CommandLineRunner start(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Youness","Ahmed","Yassin").forEach(name -> {
                        CustomerDTO customer = new CustomerDTO();
                        customer.setName(name);
                        customer.setEmail(name + "@gmail.com");
                        bankAccountService.saveCustomer(customer);
                    });
                bankAccountService.listCustomers().forEach(cus -> {
                    try {
                        bankAccountService.saveCurrentBankAccount(Math.random() * 50000,3000,cus.getId());
                        bankAccountService.saveSavingBankAccount(Math.random() * 60000,5,cus.getId());

                    } catch ( CustomerNotFoundException e) {
                        e.printStackTrace();
                    }

                });

            List<BankAccountDTO> bankAccountsDTOS = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount : bankAccountsDTOS) {
                for (int i = 0 ; i < 10; i++) {
                    String accoutId;
                    if(bankAccount instanceof BankSavingAccountDTO) {
                        accoutId = ((BankSavingAccountDTO) bankAccount).getId();
                    }
                    else {
                        accoutId = ((BankCurrentAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accoutId,1000 + Math.random() * 10000,"Credit");
                    bankAccountService.debit(accoutId,5000 * Math.random(),"Debit");
                }
            }

        };
    }

}
