package com.cpe.team24.entity;

import lombok.*;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.web.bind.annotation.CrossOrigin;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;


@CrossOrigin(origins = "*")
@Data
@Entity
@NoArgsConstructor
@Table(name = "CHECKIN_TYPE")
public class CheckInType {

    @Id
    @SequenceGenerator(name = "CHECKIN_TYPE_SEQ", sequenceName = "CHECKIN_TYPE_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHECKIN_TYPE_SEQ")
    @Column(name = "CHECKIN_TYPE_ID", unique = true, nullable = true)
    private @NonNull Long id;

    @Column(name = "Name")
    private @NonNull String name;

    @OneToOne(mappedBy = "checkInType", cascade = CascadeType.ALL)
    @JsonIgnore
    private CheckIn checkIn;
}
