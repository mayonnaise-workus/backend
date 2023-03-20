package com.tune.server.dto.request;

import lombok.*;

import java.util.Set;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppleLoginRequest {
    private String authorizationCode;
    private String user;

    private String name;
    
    private boolean marketing_agreement;

    private boolean personal_information_agreement;

    private Set<Integer> purpose_ids;

    private Set<Integer> location_ids;

    private Set<Integer> workspace_purpose_ids;
}
