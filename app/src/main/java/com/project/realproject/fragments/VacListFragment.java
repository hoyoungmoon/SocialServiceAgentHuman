package com.project.realproject.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.text.ParseException;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.project.realproject.helpers.Formatter.*;

import com.project.realproject.Vacation;
import com.project.realproject.R;
import com.project.realproject.adapters.VacListViewAdapter;
import com.project.realproject.activities.MainActivity;
import com.project.realproject.activities.MainActivity.vacType;
import com.project.realproject.helpers.DBHelper;



public class VacListFragment extends Fragment implements VacListViewAdapter.ListBtnClickListener {

       private String[] columns = new String[]{"id", "vacation", "startDate", "type", "count"};
    public DBHelper DBmanager = null;
    private ListView mListView = null;
    private VacListViewAdapter mAdapter = null;

    private Date lowerDate;
    private Date upperDate;
    private long lowerDiff = -1;
    private long upperDiff = -1;
    private String limitStartDate;
    private String limitLastDate;
    private String firstDate;
    private String lastDate;
    private vacType typeOfVac;
    private String searchStartDate;

    public VacListFragment() {
    }

    public static VacListFragment newInstance(String param1, String param2, String param3, String param4,
                                              vacType param5, String param6) {
        VacListFragment dialog = new VacListFragment();
        Bundle bundle = new Bundle(6);
        bundle.putString("limitStartDate", param1);
        bundle.putString("limitLastDate", param2);
        bundle.putString("firstDate", param3);
        bundle.putString("lastDate", param4);
        bundle.putSerializable("typeOfVac", param5);
        bundle.putString("searchStartDate", param6);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            limitStartDate = getArguments().getString("limitStartDate");
            limitLastDate = getArguments().getString("limitLastDate");
            firstDate = getArguments().getString("firstDate");
            lastDate = getArguments().getString("lastDate");
            typeOfVac = (vacType) getArguments().getSerializable("typeOfVac");
            searchStartDate = getArguments().getString("searchStartDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag2_listview, container, false);
        mListView = rootView.findViewById(R.id.listview);

        mAdapter = new VacListViewAdapter(this);
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
    public void onMenuBtnClick(int position) {
        Button button1 = mListView.findViewWithTag("revise" + position);
        Button button2 = mListView.findViewWithTag("delete" + position);
        LinearLayout linearLayout = mListView.findViewWithTag("linear" + position);
        if (button1.getVisibility() == GONE) {
            button1.setVisibility(VISIBLE);
            button2.setVisibility(VISIBLE);
            linearLayout.setVisibility(GONE);
        } else {
            button1.setVisibility(GONE);
            button2.setVisibility(GONE);
            linearLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onDeleteBtnClick(int position) {
        final Vacation vacation = (Vacation) mAdapter.getItem(position);
        new AlertDialog.Builder(getActivity())
                .setMessage("삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DBmanager.deleteVacation(vacation);
                        ((MainActivity) getActivity()).setRemainVac();
                        ((MainActivity) getActivity()).setThisMonthInfo(searchStartDate);
                        reloadListView();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onReviseBtnClick(int position) {
        Vacation vacation = (Vacation) mAdapter.getItem(position);
        int id = vacation.getId();
        FragmentManager fg = getFragmentManager();
        VacReviseFragment dialog = new VacReviseFragment().newInstance(limitStartDate, limitLastDate, firstDate,
                lastDate, typeOfVac, vacation, id, searchStartDate);
        dialog.show(fg, "dialog");
    }

    public void setListItemView(VacListViewAdapter mAdapter) {
        DBmanager = new DBHelper(getActivity());
        Cursor c = DBmanager.query(columns, DBHelper.TABLE_VACATION,
                null, null, null, null, null);

        try {
            lowerDate = formatter.parse(limitStartDate);
            upperDate = formatter.parse(limitLastDate);

            while (c.moveToNext()) {
                String type = c.getString(3);
                Date startDate = formatter.parse(c.getString(2));
                if (startDate != null) {
                    lowerDiff = startDate.getTime() - lowerDate.getTime();
                    upperDiff = upperDate.getTime() - startDate.getTime();

                    if (typeOfVac == vacType.sickVac) {
                        if (lowerDiff >= 0 && upperDiff >= 0) {
                            if (type.equals("병가") || type.equals("오전지참")
                                    || type.equals("오후조퇴") || type.equals("병가외출")) {
                                int id = c.getInt(0);
                                String vacation = c.getString(1);
                                double count = c.getDouble(4);
                                mAdapter.addItem(id, vacation, startDate, type, count);
                            }
                        }
                    } else {
                        if (lowerDiff >= 0 && upperDiff >= 0) {
                            if (type.equals("연가") || type.equals("오전반가") || type.equals("오후반가") ||
                                    type.equals("외출") || type.equals("특별휴가") || type.equals("청원휴가") || type.equals("공가")) {
                                int id = c.getInt(0);
                                String vacation = c.getString(1);
                                double count = c.getDouble(4);
                                mAdapter.addItem(id, vacation, startDate, type, count);
                            }
                        }
                    }
                } else{
                   break;
                }
            }
            c.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void reloadListView() {
        mAdapter = new VacListViewAdapter(this);
        setListItemView(mAdapter);
        mListView.setAdapter(mAdapter);
    }
}


