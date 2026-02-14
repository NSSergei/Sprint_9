package Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilmsCollections {
    private ArrayList<Movie> films = new ArrayList<>();

    public ArrayList<Movie>  fullSampleData() {
        int id;
        films.add(new Movie(1,"Пятый элемент",1997));
        films.add(new Movie(2,"Люди в чёрном",1997));
        films.add(new Movie(3,"Игра",1997));
        //films.add(new Movie("Игра",1997));

        films.add(new Movie(4,"BBC Cолнце", 2024));
        films.add(new Movie(5,"BBC Африка", 2024));
        films.add(new Movie(6,"Игра в Тумане", 2024));
        return films;
    }

    public ArrayList<Movie>  emptyFilmsList() {
        return new ArrayList<>();
    }
}



