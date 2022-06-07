package com.example.springbootsimplejpa.domain;


import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class TimestampEntityListener {
    @PrePersist
    public void prePersist(Object o) {
        System.out.println(">>> This is Work from Entity Listener prePersist");

        if (o instanceof Auditable) {
            ((Auditable) o).setCreatedAt(LocalDateTime.now());
            ((Auditable) o).setUpdatedAt(LocalDateTime.now());
        }
    }

    @PreUpdate
    public void preUpdate(Object o) {
        System.out.println(">>> This is Work from Entity Listener preUpdate");

        if (o instanceof Auditable) {
            ((Auditable) o).setUpdatedAt(LocalDateTime.now());
        }
    }

}
