package ru.investportfolio.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.investportfolio.database.entity.Portfolio;
import ru.investportfolio.database.entity.User;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Set<Portfolio>> findAllByUser(User user);
}
