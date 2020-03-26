package com.project.realproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import androidx.transition.TransitionManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.realproject.R;
import com.transitionseverywhere.Rotate;


public class ManualActivity extends AppCompatActivity implements View.OnClickListener {


    ConstraintLayout expandableView1;
    ConstraintLayout expandableView2;
    ConstraintLayout expandableView3;
    ConstraintLayout expandableView4;
    CardView manualCardView1;
    CardView manualCardView2;
    CardView manualCardView3;
    CardView manualCardView4;
    ImageView arrowBtn1;
    ImageView arrowBtn2;
    ImageView arrowBtn3;
    ImageView arrowBtn4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        manualCardView1 = findViewById(R.id.manualCardView1);
        manualCardView2 = findViewById(R.id.manualCardView2);
        manualCardView3 = findViewById(R.id.manualCardView3);
        manualCardView4 = findViewById(R.id.manualCardView4);
        expandableView1 = findViewById(R.id.expandableView1);
        expandableView2 = findViewById(R.id.expandableView2);
        expandableView3 = findViewById(R.id.expandableView3);
        expandableView4 = findViewById(R.id.expandableView4);
        arrowBtn1 = findViewById(R.id.manualButton1);
        arrowBtn2 = findViewById(R.id.manualButton2);
        arrowBtn3 = findViewById(R.id.manualButton3);
        arrowBtn4 = findViewById(R.id.manualButton4);

        //TransitionManager.beginDelayedTransition(manualEntire, new Rotate().setDuration(1500));
        manualCardView1.setOnClickListener(this);
        manualCardView2.setOnClickListener(this);
        manualCardView3.setOnClickListener(this);
        manualCardView4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.manualCardView1:
                setManualExpand(manualCardView1, arrowBtn1, expandableView1);
                break;
            case R.id.manualCardView2:
                setManualExpand(manualCardView2, arrowBtn2, expandableView2);
                break;
            case R.id.manualCardView3:
                setManualExpand(manualCardView3, arrowBtn3, expandableView3);
                break;
            case R.id.manualCardView4:
                setManualExpand(manualCardView4, arrowBtn4, expandableView4);
                break;
        }
    }

    public void setManualExpand(ViewGroup rotateContainer, View rotateButton, ViewGroup expandableView){
        TransitionManager.beginDelayedTransition(rotateContainer, new Rotate().setDuration(150));
        if (expandableView.getVisibility() == View.GONE){
            rotateButton.setRotation(180);
            expandableView.setVisibility(View.VISIBLE);
        } else {
            rotateButton.setRotation(360);
            expandableView.setVisibility(View.GONE);
        }
    }
}
