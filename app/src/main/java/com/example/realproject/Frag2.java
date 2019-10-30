package com.example.realproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Frag2 extends Fragment implements View.OnClickListener{
    private View view;
    private String firstDate;
    private String lastDate;
    private String firstRemain;
    private String secondRemain;

    TextView firstVacRemain;
    TextView secondVacRemain;
    TextView sickVacRemain;
    int count = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container,false);

        LinearLayout listButton_1 = view.findViewById(R.id.listButton);
        LinearLayout listButton_2 = view.findViewById(R.id.listButton_2);
        LinearLayout listButton_3 = view.findViewById(R.id.listButton_3);
        Button spendButton = view.findViewById(R.id.spendButton);
        Button spendButton_2 = view.findViewById(R.id.spendButton_2);
        Button spendButton_3 = view.findViewById(R.id.spendButton_3);
        firstVacRemain = view.findViewById(R.id.first_vacation_remain);
        secondVacRemain = view.findViewById(R.id.second_vacation_remain);
        sickVacRemain = view.findViewById(R.id.sick_vacation_remain);

        listButton_1.setOnClickListener(this);
        listButton_2.setOnClickListener(this);
        listButton_3.setOnClickListener(this);
        spendButton.setOnClickListener(this);
        spendButton_2.setOnClickListener(this);
        spendButton_3.setOnClickListener(this);

        firstDate = getArguments().getString("firstDate");
        lastDate = getArguments().getString("lastDate");
        firstRemain = getArguments().getString("firstRemain");
        secondRemain = getArguments().getString("secondRemain");
        firstVacRemain.setText(firstRemain);
        secondVacRemain.setText(secondRemain);

        Frag2_listview frag2_listview= new Frag2_listview(firstDate, lastDate, false);
        ListViewAdapter adapter = new ListViewAdapter(frag2_listview);
        frag2_listview.setListItemView(adapter);
        ListView test = view.findViewById(R.id.test_listview);
        test.setAdapter(adapter);
        return view;
    }

    @Override
    public void onClick(View view) {

        FragmentManager fg = getFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        switch (view.getId()) {
            // 1년차 휴가 2년차 휴가 listview에 나타나는것 다르게
            case R.id.listButton:
                setListView(R.id.fragment_container_1, R.id.first_vacation_image, fg, ft,
                        firstDate, lastDate, false);
                break;

            case R.id.listButton_2:
                setListView(R.id.fragment_container_2, R.id.second_vacation_image, fg, ft,
                        firstDate, lastDate, false);
                break;

            case R.id.listButton_3:
                setListView(R.id.fragment_container_3, R.id.sick_vacation_image, fg, ft,
                        firstDate, lastDate, true);
                break;

                // 1년차 연가, 2년차 연가 datePickerDialog 제한 어떻게 할지 결정하자. (그냥 소집~소집해제 or 소집~(소집+1)~소집해제)
            case R.id.spendButton:
                Frag2_save dialog = new Frag2_save().newInstance(firstDate, lastDate);
                dialog.show(fg, "dialog");
                break;
            case R.id.spendButton_2:
                count++;
                secondVacRemain.setText(String.valueOf(count));
                break;
            case R.id.spendButton_3:
                break;
        }
    }

    public void setListView(int viewId, int imageId, FragmentManager fg, FragmentTransaction ft,
                            String lowerBoundDate, String upperBoundDate, boolean isSickListView){
        ImageView imageView = view.findViewById(imageId);
        Fragment fragment;
        Frag2_listview frag = (Frag2_listview) fg.findFragmentByTag("filled");
        if(frag == null){
            imageView.setImageResource(R.drawable.ic_expand_less);
            fragment = new Frag2_listview(lowerBoundDate, upperBoundDate, isSickListView);
            ft.replace(viewId, fragment, "filled");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        else{
            imageView.setImageResource(R.drawable.ic_expand_more);
            fragment = new BlankFragment();
            ft.replace(viewId, fragment,"unfilled");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        }
        ft.commit();
    }
}
