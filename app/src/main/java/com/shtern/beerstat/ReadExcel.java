package com.shtern.beerstat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class ReadExcel {

    private String inputFile;
    private int type=0;
    private File inputWorkbook;
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }
    public void setInputFile(File inputFile) {
        this.inputWorkbook = inputFile;
    }
    public ReadExcel(int exceltype) {
        type = exceltype;
    }
    public ReadExcel(){
        type=0;
    }
    public void read() throws IOException  {
        Workbook w;
        Log.d("READ", "BEGIN");
        try {
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setEncoding("CP1252");
            w = Workbook.getWorkbook(inputWorkbook, wbSettings);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines

            if (type == 0) {
                for (int j = 0; j < sheet.getRows(); j++) {

                    BeerItem row = new BeerItem();
                    for (int i = 0; i < sheet.getColumns(); i++) {
                        Cell cell = sheet.getCell(i, j);
                        switch (i) {
                            case 0:
                                if (cell.getContents() != "")
                                    row.number = (int) Integer.parseInt(cell.getContents());
                            case 1:
                                row.name = cell.getContents().toString();
                            case 2:
                                row.liters = cell.getContents().toString();
                        }

                    }
                    MainActivity.beerlist.add(row);


                }

            MainActivity.adapter.notifyDataSetChanged();
        }
            if (type == 1)
                for (int j = 0; j < sheet.getRows(); j++) {
                ColorMatch row = new ColorMatch();
                for (int i = 0; i < sheet.getColumns(); i++) {
                    Cell cell = sheet.getCell(i, j);
                    switch (i) {
                        case 0:
                            if (cell.getContents() != "")
                                row.beername = cell.getContents().toString();
                        case 1:
                            row.colorstring = cell.getContents().toString();
                    }
                }
                    MainActivity.colorMatchList.add(row);
                }


        } catch (BiffException e) {
            e.printStackTrace();
        }
    }


}