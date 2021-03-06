package net.avalith.elections.services;

import net.avalith.elections.entities.BodyElections;
import net.avalith.elections.entities.CandidateWithVotes;
import net.avalith.elections.entities.ElectionResultsResponse;
import net.avalith.elections.models.Candidate;
import net.avalith.elections.models.Election;
import net.avalith.elections.models.ElectionsCandidates;
import net.avalith.elections.models.User;
import net.avalith.elections.models.Vote;
import net.avalith.elections.repositories.ElectionRepository;
import net.avalith.elections.utilities.Utilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class TestElectionService {


    @Autowired
    ElectionService electionService;

    @Autowired
    @MockBean
    private Utilities utilities;

    @Autowired
    @MockBean
    CandidateService candidateService;

    @Autowired
    @MockBean
    ElectionRepository electionRepository;

    @Autowired
    @MockBean
    ElectionsCandidatesService electionsCandidatesService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    

    @Test(expected = ResponseStatusException.class)
    public void addElectionTestFail(){
        BodyElections bodyElections = BodyElections.builder()
                .starDate(LocalDateTime.of(42069,01,31,20,00,00))
                .endDate(LocalDateTime.of(1,02,28,20,00,00))
                .candidateId(List.of(1,2))
                .build();

        electionService.addElection(bodyElections);
    }

    @Test
    public void findByIdTest(){
        Integer id =1;
        Election election = Election.builder()
                .id(1)
                .startDate(LocalDateTime.of(2020,01,31,14,00,00))
                .endDate(LocalDateTime.of(42069,02,28,20,00,00))
                .build();
        Mockito.when(electionRepository.findById(id)).thenReturn(Optional.ofNullable(election));

        Assert.assertEquals(election, electionService.findById(id));
    }

    @Test(expected = ResponseStatusException.class)
    public void findByIdTestFail(){
        Integer id = 1;
        Mockito.when(electionRepository.findById(id)).thenReturn(Optional.empty());
        electionService.findById(id);
    }

    @Test
    public void electionInProgressTest(){
        Election election = Election.builder()
                .id(1)
                .startDate(LocalDateTime.of(2020,01,31,14,00,00))
                .endDate(LocalDateTime.of(42069,02,28,20,00,00))
                .build();

        Assert.assertEquals(true, electionService.electionInProgress(election));
    }

    @Test
    public void getResultsTest(){
        Integer id =1;

        Candidate candidate =Candidate.builder()
                .name("pepe")
                .lastName("pig")
                .id(1)
                .build();

        Vote vote = Vote.builder()
                .id(1)
                .build();

        ElectionsCandidates electionsCandidates = ElectionsCandidates.builder()
                .id(1)
                .candidate(candidate)
                .votes(List.of(vote))
                .build();

        Election election = Election.builder()
                .id(1)
                .startDate(LocalDateTime.of(2020,01,31,14,00,00))
                .endDate(LocalDateTime.of(42069,02,28,20,00,00))
                .electionsCandidates(List.of(electionsCandidates))
                .build();

        Mockito.when(electionRepository.findById(id)).thenReturn(java.util.Optional.ofNullable(election));

        Long votes = 1L;
        CandidateWithVotes candidateWithVotes = CandidateWithVotes.builder()
                .id(1)
                .firstName("pepe")
                .lastName("pig")
                .votes(votes)
                .build();

        Mockito.when(electionsCandidatesService.buildCandidateWithVotes(electionsCandidates)).thenReturn(candidateWithVotes);

        ElectionResultsResponse electionResultsResponse = ElectionResultsResponse.builder()
                .id(id)
                .candidateWithVotes(List.of(candidateWithVotes))
                .totalVotes((long) electionsCandidates.getVotes().size())
                .build();

        Assert.assertEquals(electionResultsResponse, electionService.getResults(id));
    }

    @Test
    public void findElectionsInProgressTest(){

        List<Election> testElections = List.of(Election.builder()
                .id(1)
                .startDate(LocalDateTime.of(2020,01,31,14,00,00))
                .endDate(LocalDateTime.of(42069,02,28,20,00,00))
                .build());

        Mockito.when(electionRepository.findInProgressElections(any(LocalDateTime.class))).thenReturn(testElections);

        Assert.assertEquals(testElections, electionService.findElectionsInProgress());
    }
}