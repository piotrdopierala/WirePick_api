package pl.dopierala.wirepickapi.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.dopierala.wirepickapi.model.user.User;

@Component
public class UserSecurity {

    /**
     * Check if currently logged in user (principal) has user particular user id
     *
     * @param authentication from logged user will be retrieved
     * @param userId check if user has this userId
     * @return true if currently logged user has supplied userId, false otherwise
     */

    public boolean hasUserId(Authentication authentication, Long userId) {
        Object principalRaw = authentication.getPrincipal();
        User loggedUser;
        if (principalRaw instanceof User) {
            loggedUser = (User) principalRaw;
            if (loggedUser.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    //Use above as SpringEL like this:
    //http
    // .authorizeRequests()
    // .antMatchers("/user/{userId}/**")
    //      .access("@userSecurity.hasUserId(authentication,#userId)")
    //    ...
    //
    // can combine like this:
    // hasRole('admin') or @userSecurity.hasUserId(authentication,#userId)
}
