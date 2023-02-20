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
@Table(name = "member_workspace_purpose")
public class MemberWorkspacePurpose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "workspace_purpose_id", referencedColumnName = "id", insertable = false, updatable = false)
    private WorkspacePurpose purpose;
}
