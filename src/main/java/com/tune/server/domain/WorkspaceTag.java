package com.tune.server.domain;

import com.tune.server.enums.WorkspaceTagEnum;
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
@Table(name = "workspace_tag")
public class WorkspaceTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workspace_id", referencedColumnName = "id")
    private Workspace workspace;

    @Column(name = "type")
    private WorkspaceTagEnum type;

    @ManyToOne
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    private Tag tag;
}
