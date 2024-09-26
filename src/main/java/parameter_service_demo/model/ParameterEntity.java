package parameter_service_demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("parameter")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("value_")
    private String value;
}
