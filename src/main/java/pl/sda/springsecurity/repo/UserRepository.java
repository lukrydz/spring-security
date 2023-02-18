package pl.sda.springsecurity.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sda.springsecurity.user.User;

import java.util.Optional;
import java.util.UUID;
//3.tworzymy repo
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByUsername(String userName);
}
