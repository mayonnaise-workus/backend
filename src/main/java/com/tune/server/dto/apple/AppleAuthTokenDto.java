package com.tune.server.dto.apple;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AppleAuthTokenDto {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String id_token;
    @Setter
    private String user_id;
}
