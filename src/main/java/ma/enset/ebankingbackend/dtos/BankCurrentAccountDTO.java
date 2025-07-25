package ma.enset.ebankingbackend.dtos;


import lombok.Data;
import ma.enset.ebankingbackend.enums.AccountStatus;

import java.util.Date;

@Data
public class BankCurrentAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date creationDate;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double overDraft;
}
