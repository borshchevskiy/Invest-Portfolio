package ru.investportfolio.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.investportfolio.database.entity.Deal;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
}
