package Bodies;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthBody {
    private String username;
    private String password;
}
