package ch.floaty.domain.repository;

import ch.floaty.domain.model.IgcData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IIgcDataRepository extends CrudRepository<IgcData, Long> {

}
