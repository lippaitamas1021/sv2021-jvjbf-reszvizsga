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

    @GetMapping("/{id}")
    public MovieDTO getMovieById(@PathVariable("id") long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createMovie(@Valid @RequestBody CreateMovieCommand command) {
        return movieService.createMovie(command);
    }

    @PostMapping("/{id}/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createReservation(@PathVariable("id") long id, @RequestBody CreateReservationCommand command) {
        return movieService.createReservation(id, command);
    }

    @PutMapping("/{id}")
    public MovieDTO updateDateAndTimeOfAMovie(@PathVariable("id") long id, @RequestBody UpdateDateCommand command) {
        return movieService.updateDateAndTimeOfAMovie(id, command);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllMovies() {
        movieService.deleteAll();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> handleExceptionWhileReserving(IllegalStateException ise) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Bad reservation")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(ise.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> handleExceptionWhenNotFound(IllegalArgumentException iae) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Not found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(iae.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);
    }
}
