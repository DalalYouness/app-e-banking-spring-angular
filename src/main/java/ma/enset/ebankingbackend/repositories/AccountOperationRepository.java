package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.BankAccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<BankAccountOperation,Long> {
}
