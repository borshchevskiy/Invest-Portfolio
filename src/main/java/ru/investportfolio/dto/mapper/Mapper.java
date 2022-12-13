package ru.investportfolio.dto.mapper;

public interface Mapper<F, T> {

    T map(F object);
    T map(F fromObject, T toObject);
}
