package authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}