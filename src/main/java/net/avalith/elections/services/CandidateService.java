package net.avalith.elections.services;

import net.avalith.elections.entities.CandidateResponse;
import net.avalith.elections.models.Candidate;
import net.avalith.elections.repositories.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    public CandidateResponse addResponse(Candidate candidate) {
        candidateRepository.save(candidate);

        return new CandidateResponse(candidate.getId());
    }

    public Candidate findById(Integer id){

        return  candidateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontraron el candidato "+ id));
    }
}
