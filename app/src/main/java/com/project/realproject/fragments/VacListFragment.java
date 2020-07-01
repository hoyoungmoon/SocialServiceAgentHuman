package com.project.realproject.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import com.project.realproject.R;
import com.project.realproject.Vacation;
import com.project.realproject.VacationList;
import com.project.realproject.adapters.VacListViewAdapter;
import com.project.realproject.activities.MainActivity;
import com.project.realproject.activities.MainActivity.vacType;
import com.project.realproject.helpers.DBHelper;

import java.util.Calendar;


public class VacListFragment extends Fragment implements VacListViewAdapter.ListBtnClickListener {

    public DBHelper DBmanager;
    private ListView mListView = null;
    private VacListViewAdapter mAdapter = null;
    private Context mContext;

    private vacType typeOfVac;
    private VacationList vacationList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public VacListFragment(){}

    public static VacListFragment newInstance(VacationList vacationList, vacType vacType) {
        VacListFragment dialog = new VacListFragment();
        Bundle bundle = new Bundle(2);
        bundle.putParcelable("vacationList", vacationList);
        bundle.putSerializable("typeOfVac", vacType);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vacationList = getArguments().getParcelable("vacationList");
            typeOfVac = (vacType) getArguments().getSerializable("typeOfVac");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_vac_list, container, false);
        mListView = rootView.findViewById(R.id.listview);
        DBmanager = DBHelper.getInstance(mContext);

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
                        reloadListView();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        ((MainActivity) getActivity()).onDismiss(dialogInterface);
                    }
                })
                .show();
    }

    @Override
    public void onReviseBtnClick(int position) {
        Vacation vacation = (Vacation) mAdapter.getItem(position);
        FragmentManager fg = getFragmentManager();
        VacReviseFragment dialog = new VacReviseFragment().newInstance(typeOfVac, vacation);
        dialog.show(fg, "dialog");
    }

    private void setListItemView(VacListViewAdapter mAdapter) {
        mAdapter.setListItems(vacationList.getVacations());
    }

    public void reloadListView() {
        vacationList.refreshVacationList();
        mAdapter = new VacListViewAdapter(this);
        setListItemView(mAdapter);
        mListView.setAdapter(mAdapter);
    }
}


