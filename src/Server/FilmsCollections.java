package Server;

import java.util.HashMap;
import java.util.Map;

public class FilmsCollections {
    private Map<Integer, Map<Integer,String>> films = new HashMap<>();

    public void addFilm(Integer year, Integer id, String name) {
        if(!films.containsKey(year)){
            films.put(year,new HashMap<Integer,String>());
        }
        films.get(year).put(id,name);
    }

    public Map<Integer, Map<Integer,String>> fullSampleData() {
        addFilm(1997, 1,"Пятый элемент");
        addFilm(1997, 2,"Люди в чёрном");
        addFilm(1997, 3,"Игра");

        addFilm(2024, 1,"BBC Cолнце");
        addFilm(2024, 2,"BBC Африка");
        addFilm(2024, 3,"Игра в Лаву");
        return films;
    }

    public Map<Integer, Map<Integer,String>> emptyFilmsList() {
        return null;
    }
}



