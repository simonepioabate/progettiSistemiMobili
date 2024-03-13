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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class AddOrEditPlaceholderReverseGeocodingDialogFragment extends DialogFragment {


    private PoiDescriptor poiDescriptor = null;

    EditText nameEditText;
    EditText descriptionEditText;
    EditText latitudeEditText;
    EditText longitudeEditText;
    public AddOrEditPlaceholderReverseGeocodingDialogFragment() {}
    public AddOrEditPlaceholderReverseGeocodingDialogFragment(PoiDescriptor poiDescriptor) {
        this.poiDescriptor = poiDescriptor;
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.dialog_fragment_add_placeholder_reverse_geocoding, container, false);

        nameEditText = view.findViewById(R.id.namePlaceholder);
        descriptionEditText = view.findViewById(R.id.descriptionPlaceholder);
        latitudeEditText = view.findViewById(R.id.latitudePLaceholder);
        longitudeEditText = view.findViewById(R.id.longitudePLaceholder);
        TextView titleTextView = view.findViewById(R.id.titleTextView);


        Button addOrEditButton = view.findViewById(R.id.addButton);
        addOrEditButton.setOnClickListener(v -> handlerPlaceholder());

        //for edit Placeholder
        if(poiDescriptor != null)
        {
            titleTextView.setText(R.string.EDIT_PLACEHOLDER);
            addOrEditButton.setText(R.string.EDIT);
            nameEditText.setText(poiDescriptor.getName());
            descriptionEditText.setText(poiDescriptor.getDescription());
            latitudeEditText.setText(String.valueOf(poiDescriptor.getLat()));
            longitudeEditText.setText(String.valueOf(poiDescriptor.getLng()));
        }

        return view;
    }

    public void handlerPlaceholder(){

        //try to check that lat and lng are actually numbers
        try {

            String namePlaceholder = nameEditText.getText().toString();
            String descriptionPlaceholder = descriptionEditText.getText().toString();
            double latitudePlaceholder =  Double.parseDouble(latitudeEditText.getText().toString());
            double longitudePlaceholder = Double.parseDouble(longitudeEditText.getText().toString());

            if (!namePlaceholder.equals("") && !descriptionPlaceholder.equals("") &&
                    longitudePlaceholder <  180 && latitudePlaceholder <  90 &&
                    longitudePlaceholder > -180 && latitudePlaceholder > -90) {
                if (poiDescriptor == null) { //addPlaceholder

                    if (HandlerFilePlaceholder.onlyOneName(requireContext(), namePlaceholder)) {

                        addPlaceholder(new PoiDescriptor(
                                namePlaceholder, descriptionPlaceholder, latitudePlaceholder,
                                longitudePlaceholder, dateNow(), dateNow()).toString());

                        reloadFragment();

                    } else {
                        Toast.makeText(requireContext(), R.string.ERROR_EXIST_PLACEHOLDER,
                            Toast.LENGTH_SHORT).show();
                    }
                }
                else{ //editPlaceholder
                    editPlaceholder(poiDescriptor.getName(), new PoiDescriptor(
                            namePlaceholder, descriptionPlaceholder, latitudePlaceholder,
                            longitudePlaceholder, poiDescriptor.getDate(), dateNow()));

                    poiDescriptor.setLat(latitudePlaceholder);
                    poiDescriptor.setLng(longitudePlaceholder);

                    reloadFragment();
                }
            } else { Toast.makeText(requireContext(), R.string.ERROR_INVALID_DATA, Toast.LENGTH_SHORT).show(); }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), R.string.ERROR_INVALID_DATA, Toast.LENGTH_SHORT).show();
        }
    }

    private void addPlaceholder(String poiDescriptorString){

        HandlerFile.writeFile(requireContext(), getString(R.string.FILE_PLACEHOLDER), poiDescriptorString);

        dismiss();  //close DialogFragment
    }

    private void editPlaceholder(String nameOldPlaceholder, PoiDescriptor poiDescriptor){

        HandlerFilePlaceholder.deletePlaceholder(requireContext(), nameOldPlaceholder);
        HandlerFile.writeFile(requireContext(), getString(R.string.FILE_PLACEHOLDER), poiDescriptor.toString());

        dismiss();  //close DialogFragment
    }

    private void reloadFragment(){
        //reload mapFragment
        if(Objects.equals(getTag(), "MapFragment")) {
            ((MainActivity) requireActivity()).replaceFragmentWithMapFragment(
                    poiDescriptor.getLat(), poiDescriptor.getLng());
        } else { //reload SavedPlaceholderFragment
            ((MainActivity) requireActivity()).replaceFragmentWithSavedPlaceholderFragment();
        }
    }
}