package com.knubisoft.service;

import com.knubisoft.dto.FileReadSource;
import java.util.List;

public interface Convertor {
    <T> List<T> getDataFromSource(FileReadSource source, Class<T> clazz);
}
