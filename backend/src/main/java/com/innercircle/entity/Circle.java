package com.innercircle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "circles")
public class Circle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "min_age", nullable = false)
    private short minAge;

    @Column(name = "max_age")
    private Short maxAge;

    private String description;

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public short getMinAge() { return minAge; }
    public void setMinAge(short minAge) { this.minAge = minAge; }
    public Short getMaxAge() { return maxAge; }
    public void setMaxAge(Short maxAge) { this.maxAge = maxAge; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
