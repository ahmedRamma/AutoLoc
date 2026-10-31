package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;

import java.util.List;

public interface IEquipementService {

    EquipementResponse create(EquipementRequest request);

    EquipementResponse findById(Long id);

    List<EquipementResponse> findAll();

    EquipementResponse update(Long id, EquipementRequest request);

    void deleteById(Long id);
}
