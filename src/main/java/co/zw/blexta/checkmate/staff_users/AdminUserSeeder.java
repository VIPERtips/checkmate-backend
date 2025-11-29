package co.zw.blexta.checkmate.staff_users;

import co.zw.blexta.checkmate.auth.users.AuthUserService;
import co.zw.blexta.checkmate.common.dto.RegisterUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String email = "viperthehackers@gmail.com"; 
        String fullName = "System Administrator";

        try {
            // Check if user exists
            userService.getUserByEmail(email);
            System.out.println("Admin user already exists, skipping seeding.");
        } catch (Exception e) {
            // User does not exist, create
            RegisterUserDto dto = new RegisterUserDto(
                    fullName,
                    email,
                    true,  
                    1L    
            );
            userService.createUser(dto);
            System.out.println("Admin user seeded successfully.");
        }
    }
}
