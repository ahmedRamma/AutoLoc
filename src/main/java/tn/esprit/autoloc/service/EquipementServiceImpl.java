package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;
import tn.esprit.autoloc.web.mapper.EquipementMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipementServiceImpl implements IEquipementService {

    private static final String RESSOURCE = "Equipement";

    private final IEquipementRepository equipementRepository;
    private final EquipementMapper equipementMapper;

    @Override
    @Transactional
    public EquipementResponse create(EquipementRequest request) {
        Equipement equipement = equipementMapper.toEntity(request);
        return equipementMapper.toResponse(equipementRepository.save(equipement));
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementResponse findById(Long id) {
        return equipementMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementResponse> findAll() {
        return equipementRepository.findAll().stream()
                .map(equipementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public EquipementResponse update(Long id, EquipementRequest request) {
        Equipement equipement = findOrThrow(id);
        equipementMapper.updateEntity(request, equipement);
        return equipementMapper.toResponse(equipementRepository.save(equipement));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!equipementRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESSOURCE, id);
        }
        equipementRepository.deleteById(id);
    }

    private Equipement findOrThrow(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESSOURCE, id));
    }
}
