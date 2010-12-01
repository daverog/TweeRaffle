package daverog.tweeraffle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@Entity
public class WinningTweet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Key key;
    
	@ManyToOne
	public Raffle raffle;
	public String tweeter;
	
	public Text json;
	
	public Raffle getRaffle() {
		return raffle;
	}
	
	public String getJsonString() {
		return json.getValue();
	}

}
