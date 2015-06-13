package com.shtern.beerstat;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;

/**
 * Created by Алексей on 05.01.2015.
 */
public class WriteExcel {
    public void exportToExcel(List<BeerItem> beerItemList, String restaurant, String username, String filepath, Calendar cal) {


        //file path
        File excelfile = new File(filepath);

        WorkbookSettings wbSettings = new WorkbookSettings();
        //wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(excelfile, wbSettings);

            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Заказ на "+cal.get(Calendar.DAY_OF_MONTH)+"."+String.valueOf(cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.YEAR), 0);
            CellView cell;
            for(int x=0;x<4;x++)
            {
                cell=sheet.getColumnView(x);
                cell.setAutosize(true);
                sheet.setColumnView(x, cell);
            }
            try {
                sheet.addCell(new Label(0, 0, "Агент"));
                sheet.addCell(new Label(1, 0, username));

                sheet.addCell(new Label(0, 1, "Ресторан"));
                sheet.addCell(new Label(1, 1, restaurant));

                sheet.addCell(new Label(0, 2, "Заказ на"));
                sheet.addCell(new Label(1, 2, cal.get(Calendar.DAY_OF_MONTH)+"."+String.valueOf(cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.YEAR)));
                sheet.addCell(new Label(0,3,""));
                sheet.addCell(new Label(0, 4, "№"));
                sheet.addCell(new Label(1, 4, "Название"));
                sheet.addCell(new Label(2, 4, "Упаковка"));
                sheet.addCell(new Label(3, 4, "Количество"));
                int i=5;
                for (BeerItem item : beerItemList)
                {
                    if (item.count>0)
                    {
                        sheet.addCell(new Number(0,i,item.number));
                        sheet.addCell(new Label(1,i,item.name));
                        sheet.addCell(new Label(2,i,item.liters));
                        sheet.addCell(new Number(3,i,item.count));
                        i++;
                    }
                }




            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            workbook.write();

            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
