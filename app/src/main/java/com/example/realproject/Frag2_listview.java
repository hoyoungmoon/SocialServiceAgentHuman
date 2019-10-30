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
    String[] columns = new String[] {"id", "vacation", "startDate", "type", "count"};
    public vacationDBManager DBManger = null;
    ListView mListView = null;
    ListViewAdapter mAdapter = null;

    String lowerBoundDate;
    String upperBoundDate;
    Date lowerDate;
    Date upperDate;
    long lowerDiff = -1;
    long upperDiff = -1;
    boolean isSickListView;

    public Frag2_listview(String lowerBoundDate, String upperBoundDate, boolean isSickListView) {
        this.lowerBoundDate = lowerBoundDate;
        this.upperBoundDate = upperBoundDate;
        this.isSickListView = isSickListView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag2_listview, container, false);
        mAdapter = new ListViewAdapter(this);

        mListView = rootView.findViewById(R.id.listview);
        setListItemView(mAdapter);
        mListView.setAdapter(mAdapter);
        // listView scroll 할때 밖의 ScrollView의 영향 받지 않도록
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
                    { DBManger.deleteFirstVacation(firstVacation);
                    reloadListView();
                    dialog.dismiss();}

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
        FragmentManager childFragmentManager = getFragmentManager();
        Frag2_revise dialog = new Frag2_revise(firstVacation, id);
        dialog.show(childFragmentManager, "dialog");
    }

    // 생성자에서 type, upperDate, lowerDate 적어서 query에서 걸러서 listview에 나타내게 하자.
    // 또한 cardView에도 각각 걸러내진 것만 sum해서 남은 휴가 나오게하자
    public void setListItemView(ListViewAdapter mAdapter) {
        DBManger = vacationDBManager.getInstance(getActivity());
        Cursor c = DBManger.query(columns, vacationDBManager.TABLE_FIRST, null, null, null, null, null);
        Date startDate = null;
        try{
        lowerDate = formatter.parse(lowerBoundDate);
        upperDate = formatter.parse(upperBoundDate);}
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
            if(isSickListView){
                if(lowerDiff >= 0 && upperDiff >= 0 && type.equals("병가")){
                    int id = c.getInt(0);
                    String vacation = c.getString(1);
                    double count = c.getDouble(4);
                    mAdapter.addItem(id, vacation, startDate, type, count);
                }
            }
            else{
                if(lowerDiff >= 0 && upperDiff >= 0){
                int id = c.getInt(0);
                String vacation = c.getString(1);
                double count = c.getDouble(4);
                mAdapter.addItem(id, vacation, startDate, type, count);
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


