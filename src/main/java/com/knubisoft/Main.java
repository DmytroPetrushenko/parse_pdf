package com.knubisoft;

import com.knubisoft.dto.FileReadSource;
import com.knubisoft.model.RowStructure;
import com.knubisoft.service.Convertor;
import com.knubisoft.service.impl.ConvertorPdf;
import java.io.File;
import java.util.List;
import lombok.SneakyThrows;

public class Main {
    private static final Convertor convertor = new ConvertorPdf();

    @SneakyThrows
    public static void main(String[] args) {
        File file = new File("src/main/resources/sample.pdf");
        FileReadSource source = new FileReadSource(file);
        List<RowStructure> rowStructureList =
                convertor.getDataFromSource(source, RowStructure.class);
        rowStructureList.forEach(System.out::println);
    }
}
