package authentication;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
public class User {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}