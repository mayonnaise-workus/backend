package com.tune.server.domain;


import com.tune.server.enums.MemberPreferenceEnum;
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
@Table(name = "member_preference")
public class MemberPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(name = "type")
    private MemberPreferenceEnum type;

    @ManyToOne
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    private Tag tag;
}
