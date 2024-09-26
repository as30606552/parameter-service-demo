package parameter_service_demo.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import parameter_service_demo.model.ParameterEntity;

@Repository
public interface ParameterRepository extends ListCrudRepository<ParameterEntity, Long> {}
