package ma.enset.ebankingbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebankingbackend.enums.AccountStatus;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
public class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date creationDate;
    private AccountStatus status;
    @ManyToOne
    private Customer customer;
    @OneToMany(mappedBy = "bankAccount")
    private List<BankAccountOperation> operations;
}
