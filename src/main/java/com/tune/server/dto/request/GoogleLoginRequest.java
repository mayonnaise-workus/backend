package com.tune.server.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class GoogleLoginRequest {
    private String id;
    private String serverAuthCode;

    private String name;

    private boolean marketing_agreement;

    private boolean personal_information_agreement;

    private Set<Integer> purpose_ids;

    private Set<Integer> location_ids;

    private Set<Integer> workspace_purpose_ids;

}
