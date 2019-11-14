package com.example.realproject;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NULL;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.realproject.vacationDBManager.TABLE_FIRST;
import static com.example.realproject.vacationDBManager.TABLE_USER;

public class DialogFrag_DateRevise extends DialogFragment implements View.OnClickListener {

    private EditText nickNameEditText;
    private EditText firstDateEditText;
    private EditText lastDateEditText;
    private EditText mealCostEditText;
    private EditText trafficCostEditText;
    private EditText totalFirstVacEditText;
    private EditText totalSecondVacEditText;
    private EditText totalSickVacEditText;
    private EditText payDayEditText;
    private Button saveButton;
    private Button cancelButton;

    private static String[] columns = new String[]{"id", "nickName", "firstDate", "lastDate",
            "mealCost", "trafficCost", "totalFirstVac", "totalSecondVac", "totalSickVac", "payDay"};
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    DatePickerDialog firstDatePickerDialog;
    DatePickerDialog lastDatePickerDialog;
    Calendar dateCalendar;
    vacationDBManager DBmanager;

    public DialogFrag_DateRevise() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        DBmanager = vacationDBManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_frag_date_revise, container, false);

        nickNameEditText = view.findViewById(R.id.et_nickName);
        nickNameEditText.setInputType(TYPE_CLASS_TEXT);
        firstDateEditText = view.findViewById(R.id.et_firstDate);
        lastDateEditText = view.findViewById(R.id.et_lastDate);
        mealCostEditText = view.findViewById(R.id.et_mealCost);
        trafficCostEditText = view.findViewById(R.id.et_trafficCost);
        totalFirstVacEditText = view.findViewById(R.id.et_totalFirstVac);
        totalSecondVacEditText = view.findViewById(R.id.et_totalSecondVac);
        totalSickVacEditText = view.findViewById(R.id.et_totalSickVac);
        payDayEditText = view.findViewById(R.id.et_payDay);
        saveButton = view.findViewById(R.id.btn_save);
        cancelButton = view.findViewById(R.id.btn_cancel);

        // 이전 설정 미리 editText에 setting
        if(DBmanager.getDataCount(TABLE_USER) != 0){
            Cursor c = DBmanager.query(columns, vacationDBManager.TABLE_USER, null, null, null, null, null);
            c.moveToFirst();
            nickNameEditText.setText(c.getString(1));
            firstDateEditText.setText(c.getString(2));
            lastDateEditText.setText(c.getString(3));
            mealCostEditText.setText(c.getString(4));
            trafficCostEditText.setText(c.getString(5));
            totalFirstVacEditText.setText(c.getString(6));
            totalSecondVacEditText.setText(c.getString(7));
            totalSickVacEditText.setText(c.getString(8));
            payDayEditText.setText(c.getString(9));
        }

        firstDateEditText.setOnClickListener(this);
        lastDateEditText.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        mealCostEditText.setOnClickListener(this);
        trafficCostEditText.setOnClickListener(this);
        totalFirstVacEditText.setOnClickListener(this);
        totalSecondVacEditText.setOnClickListener(this);
        totalSickVacEditText.setOnClickListener(this);
        payDayEditText.setOnClickListener(this);

        firstDatePickerDialog = setDatePickerDialog(firstDateEditText);
        lastDatePickerDialog = setDatePickerDialog(lastDateEditText);

        return view;
    }

    public DatePickerDialog setDatePickerDialog(EditText dateEditText){
        final EditText someDateEditText = dateEditText;
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog returnDialog =  new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        someDateEditText.setText(formatter.format(dateCalendar
                                .getTime()));
                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        return returnDialog;
    }

    public void setNumberPickerDialog(EditText editText, int minValue, int maxValue, int step, String string){
        final String s = string;
        final EditText someNumberEditText = editText;
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final int number_of_array = (maxValue - minValue) / step + 1;
        final String[] result = new String[number_of_array];
        for (int i = 0; i < number_of_array; i++) {
            result[i] = String.valueOf(minValue + step * i);
        }

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue((maxValue - minValue) / step);
        numberPicker.setDisplayedValues(result);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int idx = numberPicker.getValue();
                someNumberEditText.setText(numberPicker.getDisplayedValues()[idx]+ s);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setView(numberPicker);
        builder.show();
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.et_firstDate:
                    firstDatePickerDialog.show();
                    break;
                case R.id.et_lastDate:
                    lastDatePickerDialog.show();
                    break;
                case R.id.btn_save:
                    // db에 user data가 있으면 수정으로 없으면 저장으로
                    // 빈칸있으면 경고
                    User user = new User();
                    user.setNickName(nickNameEditText.getText().toString());
                    user.setFirstDate(formatter.parse(firstDateEditText.getText().toString()));
                    user.setLastDate(formatter.parse(lastDateEditText.getText().toString()));
                    user.setMealCost(Integer.parseInt(mealCostEditText.getText().toString()));
                    user.setTrafficCost(Integer.parseInt(trafficCostEditText.getText().toString()));
                    user.setTotalFirstVac(Integer.parseInt(totalFirstVacEditText.getText().toString()));
                    user.setTotalSecondVac(Integer.parseInt(totalSecondVacEditText.getText().toString()));
                    user.setTotalSickVac(Integer.parseInt(totalSickVacEditText.getText().toString()));
                    user.setPayDay(Integer.parseInt(payDayEditText.getText().toString()));

                    if (DBmanager.getDataCount(TABLE_USER) == 0) {
                        DBmanager.insertUser(user);
                        dismiss();
                    } else {
                        Cursor c = DBmanager.query(columns, vacationDBManager.TABLE_USER, null, null, null, null, null);
                        c.moveToFirst();
                        int id = c.getInt(0);
                        ContentValues values = new ContentValues();
                        values.put("nickName", user.getNickName());
                        values.put("firstDate", formatter.format(user.getFirstDate()));
                        values.put("lastDate", formatter.format(user.getLastDate()));
                        values.put("mealCost", user.getMealCost());
                        values.put("trafficCost", user.getTrafficCost());
                        values.put("totalFirstVac", user.getTotalFirstVac());
                        values.put("totalSecondVac", user.getTotalSecondVac());
                        values.put("totalSickVac", user.getTotalSickVac());
                        values.put("payDay", user.getPayDay());
                        DBmanager.updateUser(id, values);

                        ((Main_Activity) getActivity()).setUserProfile();
                        if (DBmanager.getDataCount(vacationDBManager.TABLE_USER) != 0) {
                            ((Main_Activity) getActivity()).setRemainVac();
                        }
                        dismiss();
                    }
                    break;
                case R.id.et_mealCost:
                    setNumberPickerDialog(mealCostEditText,5000, 10000, 100, "원");
                    break;
                case R.id.et_trafficCost:
                    setNumberPickerDialog(trafficCostEditText, 2000, 5000, 100, "원");
                    break;
                case R.id.et_totalFirstVac:
                    setNumberPickerDialog(totalFirstVacEditText, 10, 30, 1, "개");
                    break;
                case R.id.et_totalSecondVac:
                    setNumberPickerDialog(totalSecondVacEditText, 10, 30, 1, "개");
                    break;
                case R.id.et_totalSickVac:
                    setNumberPickerDialog(totalSickVacEditText, 20, 40, 1, "개");
                    break;
                case R.id.et_payDay:
                    setNumberPickerDialog(payDayEditText, 1, 28, 1, "일(매월)");
                    break;
                case R.id.btn_cancel:
                    dismiss();
                    break;
            }
        }
        catch(ParseException e) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

}

