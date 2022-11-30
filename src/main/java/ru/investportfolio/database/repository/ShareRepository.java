package ru.investportfolio.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.investportfolio.database.entity.Share;

@Repository
public interface ShareRepository extends
        JpaRepository<Share, Long>,
        QueryShareRepository {



}
