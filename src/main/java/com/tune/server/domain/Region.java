package com.tune.server.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "region")
public class Region {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
}
