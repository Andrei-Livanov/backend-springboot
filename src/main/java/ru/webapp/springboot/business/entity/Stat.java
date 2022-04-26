package ru.webapp.springboot.business.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import ru.webapp.springboot.auth.entity.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Stat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "completed_total", updatable = false)
    private Long completedTotal;

    @Column(name = "uncompleted_total", updatable = false)
    private Long uncompletedTotal;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Stat stat = (Stat) o;
        return id != null && Objects.equals(id, stat.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
