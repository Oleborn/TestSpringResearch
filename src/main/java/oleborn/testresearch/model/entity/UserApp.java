package oleborn.testresearch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_app")
@ToString
public class UserApp {

    @Id
    private Long id;

    private String name;

    private String mail;

}


