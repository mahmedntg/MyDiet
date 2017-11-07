package com.example.company.mydiet.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.company.mydiet.R;

import java.util.List;

/**
 * Created by Mohamed Sayed on 10/20/2017.
 */

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.MyViewHolder> {
    private List<Diet> DietList;

    public DietAdapter(List<Diet> DietList) {
        this.DietList = DietList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diet_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Diet Diet = DietList.get(position);
        holder.name.setText(Diet.getName());
    }

    @Override
    public int getItemCount() {
        return DietList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, quantity, price;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.dietName);
        }
    }

}
