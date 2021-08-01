package com.nexsol.grape.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private Date birth;

    @Builder
    public User(String name, String phone, Date birth) {
        this.name = name;
        this.phone = phone;
        this.birth = birth;
    }
}
