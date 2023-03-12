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
@Table(name = "workspace")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(255) default ''")
    private String name;

    @Column(name = "sponsor", columnDefinition = "boolean default false")
    private boolean sponsor;

    @Column(name = "contact", columnDefinition = "varchar(255) default ''")
    private String contact;

    @Column(name = "profile_img", columnDefinition = "varchar(255) default ''")
    private String profileImg;

    @Column(name = "address", columnDefinition = "varchar(255) default ''")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "homepage_url", columnDefinition = "varchar(255) default ''")
    private String homepage;

    @Column(name = "info_url", columnDefinition = "varchar(255) default ''")
    private String infoUrl = "";

    @Column(name = "kakao_url", columnDefinition = "varchar(255) default ''")
    private String kakaoUrl;

    @Column(name = "naver_url", columnDefinition = "varchar(255) default ''")
    private String naverUrl;

}
