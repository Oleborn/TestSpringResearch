package oleborn.testresearch.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth_user")
@Builder
public class AuthUserApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mail;

    private String password;

    @ElementCollection
    @CollectionTable(name = "auth_user_roles", joinColumns = @JoinColumn(name = "auth_user_id"))
    @Column(name = "role")
    private List<String> roles;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "user_app_id")
    private UserApp userApp;

}
