package ma.enset.ebankingbackend.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebankingbackend.entities.BankAccount;
import ma.enset.ebankingbackend.enums.OperationType;

import java.util.Date;

@Data
public class BankAccountOperationDTO {

    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}
