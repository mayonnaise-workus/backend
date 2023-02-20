package com.tune.server.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @Size(min = 2, max = 10)
    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "marketing_agreement")
    private Boolean marketingAgreement;

    @Setter
    @Column(name = "personal_information_agreement")
    private Boolean personalInformationAgreement;

    @Setter
    @Column(name = "refresh_token")
    private String refreshToken;

    @Setter
    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    @Setter
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_preference_region_id")
    private Set<MemberPreferenceRegion> memberPreferenceRegion;

    @Setter
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_purpose_id")
    private Set<MemberPurpose> memberPurpose;

    @Setter
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_workspace_purpose_id")
    private Set<MemberWorkspacePurpose> memberWorkSpacePurpose;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
