package com.example.ManagementCompanyBot.utils.workWithFiles;

import com.example.ManagementCompanyBot.model.ReadingsEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class GenerateExcelFile {

    public ByteArrayOutputStream generateExcelReport(List<ReadingsEntity> readingsEntityList) throws IOException {
        log.info("Start generating Excel report for {} readings.", readingsEntityList.size());

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Workbook workbook = new HSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Readings");
            createHeader(sheet);
            fillData(sheet, readingsEntityList);
            workbook.write(outputStream);
            return outputStream;
        }
    }

    public void fillData(Sheet sheet, List<ReadingsEntity> readingsEntityList) {
        int rowNum = 1;
        for (ReadingsEntity readings : readingsEntityList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(readings.getId());
            row.createCell(1).setCellValue(readings.getChatId());
            row.createCell(2).setCellValue(readings.getReadingsText());
            row.createCell(3).setCellValue(readings.getDateFillingReadings().toString());
        }
    }

    public void createHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Chat ID");
        headerRow.createCell(2).setCellValue("Readings Text");
        headerRow.createCell(3).setCellValue("Date Filling Readings");
    }
}
