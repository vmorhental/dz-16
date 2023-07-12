package Bodies;

import Objects.BookingDates;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PatchBookingResponseBody {
    private String firstname;
    private String lastname;
    private Number totalprice;
    private boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;
}
