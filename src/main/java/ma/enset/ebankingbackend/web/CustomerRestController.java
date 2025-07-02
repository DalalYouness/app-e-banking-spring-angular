package ma.enset.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {

    private final BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return bankAccountService.listCustomers();
    }

}
