package com.tune.server.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_provider")
public class MemberProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expired_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime expiredAt;
}
