package com.mahmoud.watersensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;


public class ServiceAdupter extends RecyclerView.Adapter<ServiceAdupter.ServiceViweHolde> {

    private List<Service> itemsList = new ArrayList<>();

    private Context context;

    public ServiceAdupter(List<Service> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    public ServiceAdupter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ServiceViweHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServiceViweHolde(LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViweHolde holder, final int position) {
        holder.txt_name.setText(itemsList.get(position).getName());
        if (itemsList.get(position).getAmount()!=null&&!itemsList.get(position).getAmount().equals("0")){
           // holder.txt_amount.setText(itemsList.get(position).getAmount());
        }else {
          //  holder.txt_amount.setText("0");
        }

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setitemsList(List<Service> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public class ServiceViweHolde extends RecyclerView.ViewHolder {
        TextView txt_name,txt_amount;
        public ServiceViweHolde(@NonNull View itemView) {
            super(itemView);
            txt_name=itemView.findViewById(R.id.txt_name);
            txt_name=itemView.findViewById(R.id.txt_name);
        }
    }
}
