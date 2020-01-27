package net.avalith.elections.services;

import net.avalith.elections.entities.CandidateWithVotes;
import net.avalith.elections.models.ElectionsCandidates;
import net.avalith.elections.repositories.ElectionsCandidatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectionsCandidatesService {

    @Autowired
    private ElectionsCandidatesRepository electionsCandidatesRepository;

    public ElectionsCandidates addelectionscandidates(ElectionsCandidates electionsCandidates){

        return electionsCandidatesRepository.save(electionsCandidates);
    }

    public CandidateWithVotes buildCandidateWithVotes(ElectionsCandidates electionsCandidates){
        return CandidateWithVotes.builder()
                .id(electionsCandidates.getCandidate().getId())
                .lastName(electionsCandidates.getCandidate().getLastName())
                .firstName(electionsCandidates.getCandidate().getName())
                .votes(electionsCandidates.getVotes().stream().filter(
                        candidate -> candidate.getElectionsCandidates().getCandidate().getId() == electionsCandidates.getCandidate().getId()).count())
                .build();
    }
}
