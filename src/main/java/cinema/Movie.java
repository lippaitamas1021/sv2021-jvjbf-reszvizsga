package cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private long id;
    private String title;
    private LocalDateTime date;
    private int maxSpaces;
    private int freeSpaces = maxSpaces;

    public void setFreeSpaces(int reservedSeats) {
        if (reservedSeats < freeSpaces) {
            freeSpaces -= reservedSeats;
        } else {
            throw new IllegalStateException("There are not enough free places");
        }
    }
}
