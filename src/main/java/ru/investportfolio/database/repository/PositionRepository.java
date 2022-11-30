package ru.investportfolio.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.investportfolio.database.entity.Portfolio;
import ru.investportfolio.database.entity.Position;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Set<Position>> findAllByPortfolio(Portfolio portfolio);
}
