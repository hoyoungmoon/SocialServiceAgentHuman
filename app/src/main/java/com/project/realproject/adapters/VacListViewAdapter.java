package com.project.realproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.realproject.R;
import com.project.realproject.Vacation;

import java.util.ArrayList;

import static com.project.realproject.helpers.Formatter.*;


public class VacListViewAdapter extends BaseAdapter {

    private ArrayList<Vacation> listViewItemList = new ArrayList<>() ;
    private Button deleteButton;
    private Button reviseButton;
    private ImageView menuButton;


    public interface ListBtnClickListener{
        void onMenuBtnClick(int position);
        void onDeleteBtnClick(int position);
        void onReviseBtnClick(int position);
    }

    private ListBtnClickListener listBtnClickListener;

    public VacListViewAdapter(ListBtnClickListener clickListener) {
        this.listBtnClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }


    public void setListItems(ArrayList<Vacation> list){
        listViewItemList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_vac_list, parent, false);
        }

        // ViewHolder 넣어보기

        TextView titleTextView = convertView.findViewById(R.id.lv_vacation_title) ;
        TextView typeTextView = convertView.findViewById(R.id.lv_vacation_type) ;
        TextView countTextView = convertView.findViewById(R.id.lv_vacation_count) ;
        TextView startDateTextView = convertView.findViewById(R.id.lv_vacation_date) ;
        LinearLayout linearLayout = convertView.findViewById(R.id.linear_countAndType);

        deleteButton = convertView.findViewById(R.id.lv_button_delete);
        reviseButton = convertView.findViewById(R.id.lv_button_revise);
        menuButton = convertView.findViewById(R.id.lv_image_menu);

        menuButton.setTag(position);
        deleteButton.setTag("delete"+position);
        reviseButton.setTag("revise"+position);
        linearLayout.setTag("linear"+position);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listBtnClickListener != null) {
                            listBtnClickListener.onMenuBtnClick((int)v.getTag());
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listBtnClickListener != null){
                    listBtnClickListener
                            .onDeleteBtnClick(getTagOnlyInt((String) v.getTag()));
                }
            }
        });
        reviseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listBtnClickListener != null){
                    listBtnClickListener
                            .onReviseBtnClick(getTagOnlyInt((String) v.getTag()));
                }
            }
        });

        Vacation listViewItem = listViewItemList.get(position);

        titleTextView.setText(listViewItem.getVacation());
        typeTextView.setText(listViewItem.getType());
        countTextView.setText(convertMinuteToDate((int)listViewItem.getCount()));
        startDateTextView.setText((formatter.format(listViewItem.getStartDate())));

        return convertView;
    }

    public String convertMinuteToDate(int minute){
        if(minute == 480){
            return "1일";
        }
        else if(minute == 240){
            return "0.5일";
        }
        else{
            if(minute / 60 == 0){
                return minute + "분";
            }
            else if(minute % 60 == 0){
                return (minute / 60) + "시간";
            }
            else {
                return (minute / 60) + "시간 " + (minute % 60) + "분";
            }
        }
    }
}
