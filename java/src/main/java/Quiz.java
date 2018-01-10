import java.util.Dictionary;
import java.util.HashMap;
import java.util.SortedMap;

public class Quiz {
    private final String star_wars;
    public final SortedMap<String, String> questions;

    public Quiz(String star_wars, SortedMap<String, String> questions) {
        this.star_wars = star_wars;
        this.questions = questions;
    }
}
