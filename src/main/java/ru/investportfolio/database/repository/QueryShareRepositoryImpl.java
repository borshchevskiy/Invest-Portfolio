package ru.investportfolio.database.repository;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class QueryShareRepositoryImpl implements QueryShareRepository{

    private final EntityManager entityManager;

    @Override
    public void insertWithQuery(String request) {
        entityManager.createNativeQuery(request).executeUpdate();
    }
}
