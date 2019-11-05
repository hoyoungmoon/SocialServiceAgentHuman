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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

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
        DBmanager = vacationDBManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_frag_date_revise, container, false);

        nickNameEditText = view.findViewById(R.id.et_nickName);
        nickNameEditText.setInputType(TYPE_CLASS_TEXT);
        firstDateEditText = view.findViewById(R.id.et_firstDate);
        firstDateEditText.setInputType(TYPE_NULL);
        lastDateEditText = view.findViewById(R.id.et_lastDate);
        lastDateEditText.setInputType(TYPE_NULL);
        mealCostEditText = view.findViewById(R.id.et_mealCost);
        mealCostEditText.setInputType(TYPE_CLASS_NUMBER);
        trafficCostEditText = view.findViewById(R.id.et_trafficCost);
        trafficCostEditText.setInputType(TYPE_CLASS_NUMBER);
        totalFirstVacEditText = view.findViewById(R.id.et_totalFirstVac);
        totalFirstVacEditText.setInputType(TYPE_CLASS_NUMBER);
        totalSecondVacEditText = view.findViewById(R.id.et_totalSecondVac);
        totalSecondVacEditText.setInputType(TYPE_CLASS_NUMBER);
        totalSickVacEditText = view.findViewById(R.id.et_totalSickVac);
        totalSickVacEditText.setInputType(TYPE_CLASS_NUMBER);
        payDayEditText = view.findViewById(R.id.et_payDay);
        payDayEditText.setInputType(TYPE_CLASS_NUMBER);

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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
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
                try {
                    user.setFirstDate(formatter.parse(firstDateEditText.getText().toString()));
                    user.setLastDate(formatter.parse(lastDateEditText.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user.setMealCost(Integer.parseInt(mealCostEditText.getText().toString()));
                user.setTrafficCost(Integer.parseInt(trafficCostEditText.getText().toString()));
                user.setTotalFirstVac(Integer.parseInt(totalFirstVacEditText.getText().toString()));
                user.setTotalSecondVac(Integer.parseInt(totalSecondVacEditText.getText().toString()));
                user.setTotalSickVac(Integer.parseInt(totalSickVacEditText.getText().toString()));
                user.setPayDay(Integer.parseInt(payDayEditText.getText().toString()));

                if(DBmanager.getDataCount(TABLE_USER) == 0){
                    DBmanager.insertUser(user);
                    Toast.makeText(getActivity(), "저장되었습니다", Toast.LENGTH_LONG).show();
                    dismiss();
                }
                else{
                    Cursor c = DBmanager.query(columns, vacationDBManager.TABLE_USER, null, null, null, null, null);
                    c.moveToFirst();
                    int id= c.getInt(0);
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

                    Toast.makeText(getActivity(), "수정되었습니다", Toast.LENGTH_SHORT).show();
                    ((Main_Activity)getActivity()).setUserProfile();
                    if(DBmanager.getDataCount(vacationDBManager.TABLE_USER) != 0){
                        ((Main_Activity)getActivity()).setRemainVac();
                    }
                    dismiss();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

}

