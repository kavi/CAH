package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.Answer;
import dk.javacode.cah.model.Vote;


public interface VoteDao extends IDao {

	public List<Vote> findByPlayedCard(Answer playedCard);
	public Vote findById(int id);
	public void createVote(Vote vote);
}
