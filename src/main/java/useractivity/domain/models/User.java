package useractivity.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private Long id;
    private String username;
    private String avatarUrl;
    private String profileUrl;
    private String type;
}
