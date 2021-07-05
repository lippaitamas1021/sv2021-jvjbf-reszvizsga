package cinema;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private List<Movie> movies = new ArrayList<>();
    private AtomicLong idGen = new AtomicLong();
    private ModelMapper modelMapper;

    public MovieService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {
        Type targetListType = new TypeToken<List<MovieDTO>>(){}.getType();
        return modelMapper.map(movies, targetListType);
    }

    public List<MovieDTO> getMovieByTitle(String title) {
        List<Movie> result = movies.stream().filter(m -> m.getTitle().equalsIgnoreCase(title)).collect(Collectors.toList());
        Type targetListType = new TypeToken<List<MovieDTO>>(){}.getType();
        return modelMapper.map(result, targetListType);
    }

    public Movie getMovieById(long id) {
        return movies.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The movie with id: " + id + " not found"));
    }

    public MovieDTO createMovie(CreateMovieCommand command) {
        Movie movie = new Movie(idGen.incrementAndGet(),
                                command.getTitle(),
                                command.getDate(),
                                command.getMaxReservation(),
                                command.getMaxReservation());
        movies.add(movie);
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO reserveSeatsForAMovie(long id, CreateReservationCommand command) {
        Movie movie = getMovieById(id);
        movie.setFreeSpaces(movie.getMaxSeats() - command.getNumberOfReservedSeats());
        return modelMapper.map(movie, MovieDTO.class);
    }

    public List<MovieDTO> updateDateAndTimeOfAMovie(long id, UpdateDateCommand command) {
        Movie movie = getMovieById(id);
        movie.setDate(command.getDate());
        Type targetListType = new TypeToken<List<MovieDTO>>(){}.getType();
        return modelMapper.map(movie, targetListType);
    }

    public void deleteAll() {
        movies.clear();
        idGen = new AtomicLong();
    }
}
