package pl.mbalcer.managementsystem.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mbalcer.managementsystem.model.enumType.Progress;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private Integer storyPoints;
    @NotNull
    private Progress progress;

    @ManyToOne
    private Sprint sprint;
    @ManyToOne
    private User user;
}
