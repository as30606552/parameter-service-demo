package parameter_service_demo.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import parameter_service_demo.model.ParameterEntity;

import java.util.List;

@Repository
public interface ParameterRepository extends ListCrudRepository<ParameterEntity, Long> {

    @Query("select * from \"parameter\" where \"name\" = :name")
    List<ParameterEntity> findByName(String name);
}
