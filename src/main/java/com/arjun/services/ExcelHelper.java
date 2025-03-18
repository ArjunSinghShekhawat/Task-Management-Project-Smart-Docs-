package com.arjun.services;

import com.arjun.dto.AddressDto;
import com.arjun.dto.UserDto;
import com.arjun.enums.ROLE;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelHelper {

    public static ByteArrayInputStream usersToExcel(List<UserDto> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // ✅ Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "First Name", "Last Name", "Email", "Role", "Country", "Street", "City", "State", "PinCode"};

            // 🎨 Apply Bold Font for Headers
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerCellStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle); // ✅ Apply style
            }

            // ✅ Write Data Rows
            int rowIdx = 1;
            for (UserDto user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.getId().getTimestamp());
                row.createCell(1).setCellValue(user.getFirstName());
                row.createCell(2).setCellValue(user.getLastName());
                row.createCell(3).setCellValue(user.getEmail());
                row.createCell(4).setCellValue(user.getRole().name());
                row.createCell(5).setCellValue(user.getAddress().getCountry());
                row.createCell(6).setCellValue(user.getAddress().getStreet());
                row.createCell(7).setCellValue(user.getAddress().getCity());
                row.createCell(8).setCellValue(user.getAddress().getState());
                row.createCell(9).setCellValue(user.getAddress().getPinCode());
            }

            // ✅ Auto-size Columns for Better Formatting
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file: " + e.getMessage());
        }
    }
    // ✅ Convert Excel file to List<UserDto>
    public static List<UserDto> excelToUsers(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<UserDto> users = new ArrayList<>();

            // ✅ Read Rows (Skipping Header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // Skip empty rows

                UserDto user = new UserDto();
                user.setFirstName(getStringCellValue(row.getCell(1)));
                user.setLastName(getStringCellValue(row.getCell(2)));
                user.setEmail(getStringCellValue(row.getCell(3)));
                user.setRole(ROLE.valueOf(getStringCellValue(row.getCell(4)))); // ✅ Convert Role
                user.setRegistrationDate(new Date());
                user.setImageUrl(String.format(
                        "https://api.dicebear.com/5.x/initials/svg?seed=%s %s",
                        user.getFirstName(), user.getLastName()
                ));

                // ✅ Convert Address Fields
                AddressDto address = new AddressDto();
                address.setCountry(getStringCellValue(row.getCell(5)));
                address.setStreet(getStringCellValue(row.getCell(6)));
                address.setCity(getStringCellValue(row.getCell(7)));
                address.setState(getStringCellValue(row.getCell(8)));
                address.setPinCode(getStringCellValue(row.getCell(9)));

                user.setAddress(address);
                users.add(user);
            }
            return users;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }

    // ✅ Utility method to handle different cell types
    private static String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue()); // Convert numeric to string
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}
