package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.AgenceResponse;

import java.util.List;

public interface IAgenceService {

    AgenceResponse create(AgenceRequest request);

    AgenceResponse findById(Long id);

    List<AgenceResponse> findAll();

    AgenceResponse update(Long id, AgenceRequest request);

    void deleteById(Long id);
}
