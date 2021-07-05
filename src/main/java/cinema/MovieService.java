package cinema;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
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

    public List<MovieDTO> getMovies(Optional<String> title) {
        List<Movie> result = movies.stream()
                .filter(m -> title.isEmpty() || m.getTitle().equalsIgnoreCase(title.get()))
                .collect(Collectors.toList());
        Type targetListType = new TypeToken<List<MovieDTO>>(){}.getType();
        return modelMapper.map(result, targetListType);
    }

    public MovieDTO getMovieById(long id) {
        Movie result = findMovieById(id);
        return modelMapper.map(result, MovieDTO.class);
    }

    public MovieDTO createMovie(CreateMovieCommand command) {
        Movie movie = new Movie(idGen.incrementAndGet(),
                                command.getTitle(),
                                command.getDate(),
                                command.getMaxSpaces(),
                                command.getMaxSpaces());
        movies.add(movie);
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO createReservation(long id, CreateReservationCommand command) {
        Movie movie = findMovieById(id);
        movie.setFreeSpaces(command.getNumberOfReservedSeats());
        return modelMapper.map(movie, MovieDTO.class);
    }

    private Movie findMovieById(long id) {
        return movies.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The movie with id: " + id + " not found"));
    }

    public MovieDTO updateDateAndTimeOfAMovie(long id, UpdateDateCommand command) {
        Movie movie = findMovieById(id);
        movie.setDate(command.getDate());
        return modelMapper.map(movie, MovieDTO.class);
    }

    public void deleteAll() {
        movies.clear();
        idGen = new AtomicLong();
    }
}
