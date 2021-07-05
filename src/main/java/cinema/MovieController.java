package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cinema")
public class MovieController {

    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {
        return movieService.getMovies(title);
    }

    @GetMapping("/{title}")
    public List<MovieDTO> getMovieByTitle(@PathVariable("title") String title) {
        return movieService.getMovieByTitle(title);
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable("id") long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createMovie(@Valid @RequestBody CreateMovieCommand command) {
        return movieService.createMovie(command);
    }

    @PostMapping("/{id}/reserve")
    public MovieDTO reserveSeatsForAMovie(@PathVariable("id") long id, @Valid @RequestBody CreateReservationCommand command) {
        return movieService.reserveSeatsForAMovie(id, command);
    }

    @PostMapping("/{id}/refresh")
    public List<MovieDTO> updateDateAndTimeOfAMovie(@PathVariable("id") long id, @RequestBody UpdateDateCommand command) {
        return movieService.updateDateAndTimeOfAMovie(id, command);
    }

    @DeleteMapping
    public void deleteAllMovies() {
        movieService.deleteAll();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> handleExceptionWhenNotFound(IllegalStateException iae) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Cinema not found")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(iae.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> handleExceptionWhenNotFound(IllegalArgumentException iae) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Cinema bad reservation")
                .withStatus(Status.NOT_FOUND)
                .withDetail(iae.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);
    }
}
