package com.knubisoft.service.impl;

import com.knubisoft.annotation.Lookup;
import com.knubisoft.dto.FileReadSource;
import com.knubisoft.service.Convertor;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.text.PDFTextStripper;

public class ConvertorPdf implements Convertor {

    @Override
    @SneakyThrows
    public <T> List<T> getDataFromSource(FileReadSource source, Class<T> clazz) {
        File file = source.getFile();
        String string = parsePdfToString(file);

        List<String> stringList = Arrays.stream(string.split("\\r\\n"))
                .collect(Collectors.toList());
        List<String> rows = getRows(stringList, clazz);
        return rows.stream()
                .map(row -> getEntity(row, clazz))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private <T> T getEntity(String row, Class<T> clazz) {
        T instance = clazz.getConstructor().newInstance();
        Map<String, String> fieldNameRegex = findFieldNameRegex(clazz);
        fieldNameRegex.entrySet()
                .forEach(entry -> fillUpField(entry, instance, row));
        return instance;
    }

    @SneakyThrows
    private <T> void fillUpField(Map.Entry<String, String> entry, T instance, String row) {
        Field field = instance.getClass().getDeclaredField(entry.getKey());
        String result = Arrays.stream(row.split(" "))
                .filter(value -> value.matches(entry.getValue()))
                .map(value -> value.replaceAll("\\h", " "))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Regex was not fit"));
        field.setAccessible(true);
        field.set(instance, result);
    }

    private List<String> getRows(List<String> stringList, Class<?> clazz) {
        Map<String, String> fieldNameAndRegex = findFieldNameRegex(clazz);
        String commonRegex = getCommonRegex(fieldNameAndRegex);
        return stringList.stream()
                .filter(string -> string.matches(commonRegex))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private String parsePdfToString(File file) {
        PDFParser parser = new PDFParser(new RandomAccessFile(file, "r"));
        parser.parse();
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        return pdfTextStripper.getText(parser.getPDDocument());
    }

    private Map<String, String> findFieldNameRegex(Class<?> clazz) {
        Map<String, String> resultMap = new LinkedHashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (checkingFieldAnnotation(field)) {
                resultMap.put(field.getName(), field.getAnnotation(Lookup.class).regex());
            }
        }
        return resultMap;
    }

    private boolean checkingFieldAnnotation(Field field) {
        return Arrays.stream(field.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().equals(Lookup.class));
    }

    private String getCommonRegex(Map<String, String> fieldNameAndRegex) {
        return fieldNameAndRegex.values().stream()
                .reduce((x, y) -> x + " " + y)
                .orElseThrow(() -> new RuntimeException("the regex statements are absent!"));
    }
}
