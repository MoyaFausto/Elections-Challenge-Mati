package net.avalith.elections.controllers;

import net.avalith.elections.entities.BodyElections;
import net.avalith.elections.entities.BodyVote;
import net.avalith.elections.entities.ElectionResponse;
import net.avalith.elections.entities.VoteResponse;
import net.avalith.elections.services.ElectionService;
import net.avalith.elections.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/election")
public class ElectionController {

    @Autowired
    ElectionService electionService;

    @PostMapping("")
    public ElectionResponse electionResponse(@Valid @RequestBody BodyElections bodyElections){

        return electionService.addElection(bodyElections);
    }

    @Autowired
    private VoteService voteService;

    @PostMapping("/{id_election}/vote")
    public VoteResponse voteResponse(@PathVariable(name = "id_election") Integer electionid,
                                     @RequestHeader("USER_ID") String userid,
                                     @Valid @RequestBody BodyVote bodyVote){

        return voteService.addVote(electionid,userid,bodyVote);
    }
}
