package pl.dopierala.wirepickapi;

import pl.dopierala.wirepickapi.model.user.User;

public class SampleUsers {
    public static User u1 = User.builder().withFirstName("user1").build();
    public static User u2 = User.builder().withFirstName("user1").build();

    static{
        u1.setId(1);
        u2.setId(2);
    }
}
