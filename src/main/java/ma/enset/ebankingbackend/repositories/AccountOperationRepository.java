package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.BankAccount;
import ma.enset.ebankingbackend.entities.BankAccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<BankAccountOperation,Long> {
    List<BankAccountOperation> findByBankAccountId(String bankAccountId);
    Page<BankAccountOperation> findByBankAccountId(String accountId, Pageable pageable);
}
