package com.example.springbootsimplejpa.domain;


import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
@Entity
@EntityListeners(value = {TimestampEntityListener.class})
//@Table(name = "orders")
public class OrderModel implements Auditable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String orderCode;

    @NotNull
    private String userMail;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    /*
//    Listeners 추가
//     */
//    @PrePersist
//    public void prePersist() {
//        System.out.println(">>> prePersist");
//    }
//
//    @PostPersist
//    public void postPersist() {
//        System.out.println(">>> postPersist");
//        this.setCreatedAt(LocalDateTime.now());
//        this.setUpdatedAt(LocalDateTime.now());
//    }

    @PreUpdate
    public void preUpdate() {
        System.out.println(">>> preUpdate");
    }

//    @PostUpdate
//    public void postUpdate() {
//        System.out.println(">>> postUpdate");
//        this.setUpdatedAt(LocalDateTime.now());
//    }

    @PreRemove
    public void preRemove() {
        System.out.println(">>> preRemove");
    }

    @PostRemove
    public void postRemove() {
        System.out.println(">>> postRemove");
    }

    @PostLoad
    public void postLoad() {
        System.out.println(">>> postLoad");
    }


}
