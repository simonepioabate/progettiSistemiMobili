package com.example.placeremindermap;

import static com.example.placeremindermap.HandlerDate.dateNow;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddPlaceholderDialogFragment extends DialogFragment {

    private final double lat;
    private final double lng;
    public AddPlaceholderDialogFragment(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //color background transparent
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.dialog_fragment_add_forward_geocoding_placeholder, container, false);
        View viewActivity = inflater.inflate(R.layout.activity_main, container, false);

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> addPlaceholder(view, viewActivity));

        return view;
    }

    public void addPlaceholder(View view, View viewActivity) {
        
        EditText nameEditText = view.findViewById(R.id.namePlaceholder);
        EditText descriptionEditText = view.findViewById(R.id.descriptionPlaceholder);

        String namePlaceholder = nameEditText.getText().toString();
        String descriptionPlaceholder = descriptionEditText.getText().toString();

        //check if all data has been entered
        if (!namePlaceholder.equals("") && !descriptionPlaceholder.equals(""))
        {
            if(HandlerFilePlaceholder.onlyOneName(requireContext(), namePlaceholder)){

                addPlaceholder(new PoiDescriptor(
                        namePlaceholder, descriptionPlaceholder, lat, lng, dateNow(), dateNow()).toString());

                reloadFragment(viewActivity);

            }else {
                Toast.makeText(requireContext(), R.string.ERROR_EXIST_PLACEHOLDER,
                        Toast.LENGTH_SHORT).show();
            }
        } else{ Toast.makeText(requireContext(), R.string.ERROR_INVALID_DATA,  Toast.LENGTH_SHORT).show(); }
    }

    public void addPlaceholder(String poiDescriptorString){
        HandlerFile.writeFile(requireContext(), getString(R.string.FILE_PLACEHOLDER), poiDescriptorString);

        dismiss();  //close DialogFragment
    }

    public void reloadFragment(View viewActivity){
        //reload mapFragment
        ((MainActivity) requireActivity()).replaceFragmentWithMapFragment(lat, lng);
        //reload the menu
        ((MainActivity) requireActivity()).closeOnClick(viewActivity);
    }


}