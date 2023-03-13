package com.tune.server.domain;

import com.google.api.client.util.DateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "magazine")
@EntityListeners(AuditingEntityListener.class)
public class Magazine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "content_img")
    private String contentImg;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
