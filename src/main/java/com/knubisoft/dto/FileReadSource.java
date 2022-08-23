package com.knubisoft.dto;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileReadSource {
    private File file;
}
