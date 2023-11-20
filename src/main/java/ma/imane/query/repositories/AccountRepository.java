package ma.imane.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ma.imane.query.entities.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
}
