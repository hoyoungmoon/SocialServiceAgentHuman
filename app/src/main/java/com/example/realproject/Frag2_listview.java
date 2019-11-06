package com.example.realproject;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;


public class Frag2_listview extends Fragment implements  ListViewAdapter.ListBtnClickListener {

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    private String[] columns = new String[] {"id", "vacation", "startDate", "type", "count"};
    public vacationDBManager DBManger = null;
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;

    private Date lowerDate;
    private Date upperDate;
    private long lowerDiff = -1;
    private long upperDiff = -1;
    private String limitStartDate;
    private String limitLastDate;
    private String firstDate;
    private String lastDate;
    private int numOfYear;
    private String searchStartDate;

    public Frag2_listview() {}

    public static Frag2_listview newInstance(String param1, String param2, String param3, String param4,
                                             int param5, String param6){
        Frag2_listview dialog = new Frag2_listview();
        Bundle bundle = new Bundle(6);
        bundle.putString("limitStartDate", param1);
        bundle.putString("limitLastDate", param2);
        bundle.putString("firstDate", param3);
        bundle.putString("lastDate", param4);
        bundle.putInt("numOfYear", param5);
        bundle.putString("searchStartDate", param6);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            limitStartDate = getArguments().getString("limitStartDate");
            limitLastDate = getArguments().getString("limitLastDate");
            firstDate = getArguments().getString("firstDate");
            lastDate = getArguments().getString("lastDate");
            numOfYear = getArguments().getInt("numOfYear");
            searchStartDate = getArguments().getString("searchStartDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag2_listview, container, false);
        mListView = rootView.findViewById(R.id.listview);

        mAdapter = new ListViewAdapter(this);
        setListItemView(mAdapter);
        mListView.setAdapter(mAdapter);


        mListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onMenuBtnClick(int position){
        Button button1 = mListView.findViewWithTag("revise"+position);
        Button button2 = mListView.findViewWithTag("delete"+position);
        if (button1.getVisibility() == GONE) {
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
        } else {
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteBtnClick(int position) {
        final FirstVacation firstVacation = (FirstVacation) mAdapter.getItem(position);
        new AlertDialog.Builder(getActivity())
                .setMessage("삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DBManger.deleteFirstVacation(firstVacation);
                        ((Main_Activity)getActivity()).setRemainVac();
                        ((Main_Activity)getActivity()).setThisMonthInfo(searchStartDate);
                        reloadListView();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onReviseBtnClick(int position) {
        FirstVacation firstVacation = (FirstVacation) mAdapter.getItem(position);
        int id = firstVacation.getId();
        FragmentManager fg = getFragmentManager();
        Frag2_revise dialog = new Frag2_revise().newInstance(limitStartDate, limitLastDate, firstDate,
                lastDate, numOfYear, firstVacation, id, searchStartDate);
        dialog.show(fg, "dialog");
    }

    public void setListItemView(ListViewAdapter mAdapter){
        DBManger = vacationDBManager.getInstance(getActivity());
        Cursor c = DBManger.query(columns, vacationDBManager.TABLE_FIRST, null, null, null, null, null);
        Date startDate = null;

        try{
        lowerDate = formatter.parse(limitStartDate);
        upperDate = formatter.parse(limitLastDate);}
        catch(ParseException e){
            e.printStackTrace();
        }
        while (c.moveToNext()) {
            String type = c.getString(3);
            try {
                startDate = formatter.parse(c.getString(2));
                lowerDiff = startDate.getTime() - lowerDate.getTime();
                upperDiff = upperDate.getTime() - startDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(numOfYear == 3){
                if(lowerDiff >= 0 && upperDiff >= 0){
                    if(type.equals("병가")||type.equals("오전지참")
                            ||type.equals("오후조퇴")||type.equals("병가외출")) {
                        int id = c.getInt(0);
                        String vacation = c.getString(1);
                        double count = c.getDouble(4);
                        mAdapter.addItem(id, vacation, startDate, type, count);
                    }
                }
            }
            else{
                if(lowerDiff >= 0 && upperDiff >= 0){
                    if(type.equals("연가")||type.equals("오전반가")
                            ||type.equals("오후반가")||type.equals("외출")) {
                        int id = c.getInt(0);
                        String vacation = c.getString(1);
                        double count = c.getDouble(4);
                        mAdapter.addItem(id, vacation, startDate, type, count);
                    }
                }
            }
        }
        c.close();
    }

    public void reloadListView(){
        ListViewAdapter adapter = new ListViewAdapter(this);
        setListItemView(adapter);
        mListView.setAdapter(adapter);
    }
}


