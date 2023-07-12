package Bodies;

import Objects.BookingObject;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CreateBookingRequestResponse {
private Number bookingid;
private BookingObject booking;
}
