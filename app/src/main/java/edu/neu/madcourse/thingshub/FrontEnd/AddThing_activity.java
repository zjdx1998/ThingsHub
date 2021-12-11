package edu.neu.madcourse.thingshub.FrontEnd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import edu.neu.madcourse.thingshub.Model.Date;
import edu.neu.madcourse.thingshub.R;
import top.defaults.colorpicker.ColorPickerPopup;

public class AddThing_activity extends AppCompatActivity {

    public static final String THINGS_NAME = "thingsName";
    public static final String START_DATE= "startDate";
    public static final String END_DATE = "endDate";
    public static final String IS_COMPLETED = "isCompleted";
    public static final String COLOR = "color";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";


    private DatePickerDialog datePickerDialog;
    private Button fromDateBtn;
    private Button toDateBtn;
    private Button colorPickerBtn;
    private EditText inputTitle;
    private FloatingActionButton fab;
    public String title;
    public int colorPicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addthings);

        fromDateBtn = findViewById(R.id.fromDateBtn);
        toDateBtn = findViewById(R.id.toDateBtn);

        fromDateBtn.setOnClickListener(v -> {
            initDatePicker(fromDateBtn);
            openDatePicker();
        });
        fromDateBtn.setText(getCurDate());

        toDateBtn.setOnClickListener(v -> {
            initDatePicker(toDateBtn);
            openDatePicker();
        });
        toDateBtn.setText(getCurDate());

        colorPickerBtn = findViewById(R.id.colorPickerBtn);
        colorPickerBtn.setOnClickListener(view -> new ColorPickerPopup.Builder(AddThing_activity.this)
                .initialColor(Color.BLUE)
                .enableBrightness(true)
                .enableAlpha(true)
                .okTitle("CHOOSE")
                .cancelTitle("CANCEL")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(view, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        colorPicked = color;
                        System.out.println(colorPicked);
                        colorPickerBtn.setBackgroundColor(color);

                    }
                }));

        inputTitle = findViewById(R.id.inputTitle);
        fab = findViewById(R.id.addThing);

        fab.setOnClickListener(view -> enterThings());

    }

    public void enterThings() {

        title = inputTitle.getText().toString();

        if (!title.isEmpty() || title == null ) {
            Intent data = new Intent();
            data.putExtra(THINGS_NAME, title);
            data.putExtra(START_DATE, fromDateBtn.getText().toString());
            data.putExtra(END_DATE, toDateBtn.getText().toString());
            data.putExtra(IS_COMPLETED, false);
            data.putExtra(COLOR, colorPicked);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private String getCurDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return (new Date(year,month,day)).toKey();
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private void initDatePicker(Button button) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                String date = makeDateString(day, month, year);
                button.setText(date);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1) return "JAN";
        if (month == 2) return "FEB";
        if (month == 3) return "MAR";
        if (month == 4) return "APR";
        if (month == 5) return "MAY";
        if (month == 6) return "JUN";
        if (month == 7) return "JUL";
        if (month == 8) return "AUG";
        if (month == 9) return "SEP";
        if (month == 10) return "OCT";
        if (month == 11) return "NOV";
        if (month == 12) return "DEC";

        return "JAN";
    }

    private void openDatePicker(){
        datePickerDialog.show();
    }


}