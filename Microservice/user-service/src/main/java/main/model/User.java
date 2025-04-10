package main.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;
//where to check?
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
public class User {
    @Id @GeneratedValue(strategy = IDENTITY)
    private long userId;
    @Column(length = 50,nullable = false) @Size(min = 1, max = 50)
    private String name;
    @Column(length = 50,nullable = false) @NotBlank @Email @Size(max = 50)
    private String email;

}
