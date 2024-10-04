package ch.floaty.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity()
@Table(name = "t_user")
public class User {
    @Id
    private Long id;
    private String name;
}

