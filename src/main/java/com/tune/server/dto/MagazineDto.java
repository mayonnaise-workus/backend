package com.tune.server.dto;


import com.tune.server.domain.Magazine;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MagazineDto {
    private String title_image;

    private String content_image;

    public static MagazineDto of(Magazine magazine) {
        return MagazineDto.builder()
                .title_image(magazine.getProfileImg())
                .content_image(magazine.getContentImg())
                .build();
    }

    public static List<MagazineDto> of(List<Magazine> magazineList) {
        return magazineList.stream()
                .map(MagazineDto::of)
                .collect(Collectors.toList());
    }
}
