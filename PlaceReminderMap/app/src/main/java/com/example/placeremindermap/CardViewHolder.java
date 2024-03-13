package com.example.placeremindermap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder{
    private final Context context;
    private final Activity activity;
    private final FragmentManager fragmentManager;

    public TextView nameTextView;
    public TextView descriptionTextView;
    public TextView lastUpdateTextView;
    public TextView creationDateTextView;
    public ImageButton deleteButton;
    public ImageButton editButton;


    public CardViewHolder(View itemView, Context context, Activity activity, FragmentManager fragmentManager) {
        super(itemView);

        this.context = context;
        this.activity = activity;
        this.fragmentManager = fragmentManager;

        nameTextView = itemView.findViewById(R.id.namePlaceholderTextView);
        descriptionTextView = itemView.findViewById(R.id.descriptionPlaceholderTextView);
        lastUpdateTextView = itemView.findViewById(R.id.lastUpdateTextView);
        creationDateTextView = itemView.findViewById(R.id.creationDateTextView);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        editButton = itemView.findViewById(R.id.editButton);

        deleteButton.setOnClickListener(this::deleteOnClick);
        editButton.setOnClickListener(this::editOnClick);
    }

    private void editOnClick(View view) {

        PoiDescriptor poiDescriptor = HandlerFilePlaceholder.recoveryPlaceholder(context, nameTextView.getText().toString());

        AddOrEditPlaceholderReverseGeocodingDialogFragment dialogFragment =
                new AddOrEditPlaceholderReverseGeocodingDialogFragment(poiDescriptor);


        dialogFragment.show(fragmentManager, "SavedPlaceholdersFragment");
    }

    private void deleteOnClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.CONFIRM_DELETE);
        builder.setMessage(R.string.REQUEST_CONFIRM_DELETE);

        // Add the Confirm button
        builder.setPositiveButton(R.string.CONFIRM, (dialog, which) -> {
            // The user has confirmed the deletion
            HandlerFilePlaceholder.deletePlaceholder(context, nameTextView.getText().toString());

            //reload SavedPlaceholderFragment
            ((MainActivity) activity).replaceFragmentWithSavedPlaceholderFragment();
        });

        // Add the cancel button
        builder.setNegativeButton(R.string.CANCEL, (dialog, which) -> {
            // The user has canceled the deletion
            dialog.dismiss();
        });

        // Show the AlertDialog
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }


    public void bind(PoiDescriptor item) {
        nameTextView.setText(item.getName());
        descriptionTextView.setText(item.getDescription());
        lastUpdateTextView.setText(activity.getString(R.string.LAST_UPDATE, item.getLastUpdate()));
        creationDateTextView.setText(activity.getString(R.string.CREATION_DATE, item.getDate()));

    }
}