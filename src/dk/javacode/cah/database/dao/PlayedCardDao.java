package dk.javacode.cah.database.dao;

import java.util.List;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.ActiveBlackCard;
import dk.javacode.cah.model.Answer;


public interface PlayedCardDao extends IDao {

	public List<Answer> findByActiveBlackCard(ActiveBlackCard activeBlackCard);
	public Answer findById(int id);
}
