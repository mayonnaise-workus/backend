package com.tune.server.service.magazine;

import com.tune.server.dto.response.MagazineResponse;
import com.tune.server.repository.MagazineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MagazineServiceImpl implements MagazineService {

    private MagazineRepository magazineRepository;

    @Override
    public MagazineResponse getAllMagazine() {
        return MagazineResponse.of(magazineRepository.findAll());
    }
}
