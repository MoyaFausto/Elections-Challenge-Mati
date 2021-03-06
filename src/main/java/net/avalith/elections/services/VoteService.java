package net.avalith.elections.services;

import net.avalith.elections.entities.BodyFakeVote;
import net.avalith.elections.entities.BodyVote;
import net.avalith.elections.entities.FakeUserResponse;
import net.avalith.elections.entities.VoteResponse;
import net.avalith.elections.models.Election;
import net.avalith.elections.models.ElectionsCandidates;
import net.avalith.elections.models.User;
import net.avalith.elections.models.Vote;
import net.avalith.elections.repositories.VoteRepository;
import net.avalith.elections.utilities.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ElectionService electionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private Utilities utilities;

    private final Logger logger = LoggerFactory.getLogger(VoteService.class);

    public VoteResponse addVote(Integer electionid, String userid, BodyVote bodyVote) {
        Election election = electionService.findById(electionid);

        ElectionsCandidates electionsCandidates = election.getElectionsCandidates().stream().filter(
                it -> it.getCandidate().getId() == bodyVote.getCandidateid()
        ).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El candidato no participa de la eleccion"));

        User user = userService.findById(userid);

        if (electionService.electionInProgress(election) && didNotVote(electionid, user)) {
            Vote vote = Vote.builder()
                    .electionsCandidates(electionsCandidates)
                    .user(user)
                    .build();
            voteRepository.save(vote);

            return new VoteResponse("Voto ingresado con éxito");
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La eleccion numero " + electionid + " no esta en progeso");
    }

    public Vote findById(Integer id) {

        return voteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontro el voto"));
    }

    public FakeUserResponse addFakeVotes(Integer electionid, BodyFakeVote bodyFakeVote) {
        Election election = electionService.findById(electionid);
        ElectionsCandidates electionsCandidates = election.getElectionsCandidates().stream().filter(
                it -> it.getCandidate().getId() == bodyFakeVote.getCandidateId()
        ).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El candidato no participa de la eleccion"));

        if (electionService.electionInProgress(election)) {
            List<User> usuariosFalsos = userService.findAllFakeUsers();

            usuariosFalsos.stream()
                    .filter(it -> didNotVote(electionid, it))
                    .forEach(
                    it -> {
                            try {
                                voteRepository.save(Vote.builder()
                                        .user(it)
                                        .electionsCandidates(electionsCandidates)
                                        .build());
                            }
                            catch (DataIntegrityViolationException e) {
                                logger.info("No se creo un voto del usuario " +it.getId());
                            }
                        }
                    );

            return new FakeUserResponse("Votos generados correctamente");
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La eleccion numero " + electionid + " no esta en progeso");
    }

    public Boolean didNotVote(Integer electionId, User user){

        return user.getVote().stream().noneMatch(it-> it.getElectionsCandidates().getElection().getId() == electionId);
    }

    public Double getTotalVotes(List<ElectionsCandidates> electionsCandidates){

        return electionsCandidates.stream().mapToDouble(
                x -> x.getVotes().size())
                .sum();
    }
}