package com.example.sqllite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnClear;
    EditText startRoute, endRoute, etDate;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        startRoute = (EditText) findViewById(R.id.startRoute);
        endRoute = (EditText) findViewById(R.id.endRoute);
        etDate = (EditText) findViewById(R.id.etDate);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        UpdateTable();

        startRoute.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    startRoute.setHint("");
                else
                    startRoute.setHint("Начало маршрута");
            }
        });
        endRoute.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    endRoute.setHint("");
                else
                    endRoute.setHint("Конец маршрута");
            }
        });
        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    etDate.setHint("");
                else
                    etDate.setHint("Дата");
            }
        });
    }
    public void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int startRouteIndex = cursor.getColumnIndex(DBHelper.KEY_START);
            int endRouteIndex = cursor.getColumnIndex(DBHelper.KEY_END);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do {
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID = new TextView(this);
                params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                outputID.setTextSize(12);
                dbOutputRow.addView(outputID);

                TextView outputStart = new TextView(this);
                params.weight = 3.0f;
                outputStart.setLayoutParams(params);
                outputStart.setText(cursor.getString(startRouteIndex));
                outputStart.setTextSize(12);
                dbOutputRow.addView(outputStart);

                TextView outputEnd = new TextView(this);
                params.weight = 3.0f;
                outputEnd.setLayoutParams(params);
                outputEnd.setText(cursor.getString(endRouteIndex));
                outputEnd.setTextSize(12);
                dbOutputRow.addView(outputEnd);

                TextView outputDate = new TextView(this);
                params.weight = 3.0f;
                outputDate.setLayoutParams(params);
                outputDate.setText(cursor.getString(dateIndex));
                outputDate.setTextSize(12);
                dbOutputRow.addView(outputDate);

                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight = 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить\nзапись");
                deleteBtn.setTextSize(12);
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                dbOutput.addView(dbOutputRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                String start = startRoute.getText().toString();
                String end = endRoute.getText().toString();
                String date = etDate.getText().toString();
                contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_START, start);
                contentValues.put(DBHelper.KEY_END, end);
                contentValues.put(DBHelper.KEY_DATE, date);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                startRoute.setText("");
                endRoute.setText("");
                etDate.setText("");
                UpdateTable();
                break;
            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutput = findViewById(R.id.dbOutput);
                dbOutput.removeAllViews();
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();

                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf(v.getId())});

                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int startRouteIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_START);
                    int endRouteIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_END);
                    int dateIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_DATE);
                    int realID = 1;
                    do{
                        if(cursorUpdater.getInt(idIndex) > idIndex){
                            contentValues.put(DBHelper.KEY_ID, realID);
                            contentValues.put(DBHelper.KEY_START, cursorUpdater.getString(startRouteIndex));
                            contentValues.put(DBHelper.KEY_END, cursorUpdater.getString(endRouteIndex));
                            contentValues.put(DBHelper.KEY_DATE, cursorUpdater.getString(dateIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realID++;
                    } while(cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast() && (cursorUpdater.getInt(idIndex) == realID)){
                        database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }
                break;
        }
    }
}