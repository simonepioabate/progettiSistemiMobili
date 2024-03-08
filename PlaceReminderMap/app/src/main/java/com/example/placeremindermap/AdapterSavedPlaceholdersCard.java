package com.example.placeremindermap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterSavedPlaceholdersCard extends RecyclerView.Adapter<CardViewHolder> {
    private final List<PoiDescriptor> dataList;
    private final Context context;
    private final Activity activity;
    private final FragmentManager fragmentManager;

    public AdapterSavedPlaceholdersCard(List<PoiDescriptor> dataList, Context context, Activity activity, FragmentManager fragmentManager) {
        this.dataList = dataList;
        this.dataList.sort(new HandlerDate.DateComparator());

        //to manage the buttons of each card
        this.context = context;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_saved_placeholder, parent, false);
        return new CardViewHolder(view, context, activity, fragmentManager);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        PoiDescriptor item = dataList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



}
