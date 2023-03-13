package com.tune.server.dto.response;

import com.tune.server.domain.Magazine;
import com.tune.server.dto.MagazineDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MagazineResponse {
    private List<MagazineDto> list;

    public static MagazineResponse of(List<Magazine> magazineList) {
        return MagazineResponse.builder()
                .list(MagazineDto.of(magazineList))
                .build();
    }
}
