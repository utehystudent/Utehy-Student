package com.example.utehystudent.activity.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.ContactAdapter;
import com.example.utehystudent.model.Contact;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ImportExcelContact_Activity extends AppCompatActivity implements Serializable {
    final int request_code = 1;
    ArrayList<Contact> listContactImport;
    Toolbar toolbar;
    RecyclerView rcv;
    ArrayList<Contact> listContact;
    ContactAdapter contactAdapter;
    Button btnChonFile;
    ProgressBar prgBar;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_import_excel_contact);

        Init();
    }

    private void Init() {
        toolbar = findViewById(R.id.ImportExcel_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("IMPORT DỮ LIỆU");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        db = FirebaseFirestore.getInstance();
        prgBar = findViewById(R.id.ImportExcel_prgBar);
        listContactImport = new ArrayList<>();
        rcv = findViewById(R.id.ImportExcel_rcv);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(itemDecoration);
        listContact = new ArrayList<>();
        contactAdapter = new ContactAdapter(this);
        btnChonFile = findViewById(R.id.ImportExcel_btnChonFile);
        //
        contactAdapter.setData(listContact);
        rcv.setAdapter(contactAdapter);

        Event();
    }

    private void Event() {
        btnChonFile.setOnClickListener(view -> {
            rcv.setVisibility(View.GONE);
            listContact.clear();
            contactAdapter.setData(listContact);
            importExcel();
        });
    }

    //functions import data contact
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Toast.makeText(ImportExcelContact_Activity.this, "Pick file success", Toast.LENGTH_SHORT).show();
            readExcelFile(data.getData());
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void readExcelFile(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();

            InputStream inputStream = contentResolver.openInputStream(Uri.parse(uri.toString()));

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rowCount = sheet.getPhysicalNumberOfRows();
            Log.d("vvv", "readExcelFile: NUM COUNT ROW: "+rowCount);

            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            for (int r = 1; r < rowCount; r++) {
                Row row = sheet.getRow(r);
                int cellCount = row.getPhysicalNumberOfCells();

                for (int c = 0; c < cellCount; ++c) {
                    if (c > 8) {
                        Toast.makeText(ImportExcelContact_Activity.this, "File không đúng định dạng", Toast.LENGTH_SHORT).show();
                        break;
                    }else {
                        String value = getCellAsString(row, c, formulaEvaluator);
                        sb.append(value + "#");
                        Log.d("vvv", "readExcelFile: "+sb);
                    }
                }
                sb.append(",");
            }
            parseStringBuilder(sb);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseStringBuilder(StringBuilder mStringBuilder){

        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(",");

        //Add to the ArrayList<XYValue> row by row
        for(int i=0; i < rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split("#");

            //use try catch to make sure there are no "" that try to parse into doubles.
            try{
                Contact contact = new Contact();

                contact.setFaculty_ID(columns[0]);
                contact.setDepartment(columns[1]);
                contact.setName(columns[2]);
                contact.setPosition(columns[3]);
                contact.setEmail(columns[4]);
                contact.setPhone(columns[5]);
                contact.setAvt_link(columns[6]);

                listContact.add(contact);

            }catch (NumberFormatException e){
                Log.e("xxx", "parseStringBuilder: NumberFormatException: " + e.getMessage());
            }
        }

        contactAdapter.setData(listContact);
        rcv.setVisibility(View.VISIBLE);
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {

            Log.e("xxx", "getCellAsString: NullPointerException: " + e.getMessage() );
        }
        return value;
    }

    private void importExcel() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("*/*");
        startActivityForResult(it, request_code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_import_excel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_import_excel_itXong:
                addContact();
                break;
            case R.id.menu_import_excel_itHD:
                openTipImport();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openTipImport() {

    }

    private void addContact() {
        if (listContact.size() == 0) {
            Toast.makeText(this, "Không có liên hệ nào để thêm!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Contact contact : listContact) {
            db.collection("Contact")
                    .add(contact);
        }
        Toast.makeText(this, "Thêm liên hệ thành công", Toast.LENGTH_SHORT).show();
        listContact.clear();
        contactAdapter.setData(listContact);
    }
}