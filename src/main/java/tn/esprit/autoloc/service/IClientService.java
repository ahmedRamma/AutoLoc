package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.ClientRequest;
import tn.esprit.autoloc.web.dto.ClientResponse;

import java.util.List;

public interface IClientService {

    ClientResponse create(ClientRequest request);

    ClientResponse findById(Long id);

    List<ClientResponse> findAll();

    ClientResponse update(Long id, ClientRequest request);

    void deleteById(Long id);
}
