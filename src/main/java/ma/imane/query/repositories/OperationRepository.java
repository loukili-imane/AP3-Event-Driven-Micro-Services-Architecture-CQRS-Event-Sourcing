package ma.imane.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ma.imane.query.entities.Operation;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
