package daverog.tweeraffle;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import static com.google.appengine.api.datastore.KeyFactory.keyToString;
import com.google.appengine.api.datastore.Key;

@Entity
public class Raffle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Key key;
	
	public String hashtag;
    
	public Date open;

	@OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	public List<WinningTweet> winningTweets;

	public String getHashtag() {
		return hashtag;
	}

	public List<WinningTweet> getWinningTweets() {
		return winningTweets;
	}

	public Date getOpen() {
		return open;
	}

	public Key getKey() {
		return key;
	}
	
	public String getId() {
		return keyToString(key);
	}
	
}