package ru.morozov.bill.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    @SequenceGenerator(name="accounts_gen", sequenceName="accounts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy=SEQUENCE, generator="accounts_gen")
    private Long id;

    private Long userId;
    private Float balance;
}
