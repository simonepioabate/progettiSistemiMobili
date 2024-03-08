package com.example.placeremindermap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ViewSwitcher;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    SettingsData settingsData;
    Switch typeCoordinateSwitch;
    EditText latitudeEditText;
    EditText longitudeEditText;
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //recover information from the file "settings.txt"
        settingsData =  HandlerFileSettings.readFile(requireContext());

        //editText for Reverse Geocoding
        latitudeEditText = view.findViewById(R.id.setLatitudeEditText);
        longitudeEditText = view.findViewById(R.id.setLongitudeEditText);


        //for Forward Geocoding
        Spinner selectPlaceholderSpinner = view.findViewById(R.id.selectPlaceholderSpinner);
        List<String> namePlaceholderList =  HandlerFilePlaceholder.readOnlyNameFile(requireContext());
        namePlaceholderList.add(0, "Seleziona un elemento");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
               namePlaceholderList);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectPlaceholderSpinner.setAdapter(spinnerAdapter);

        //select a placeholder like center of the map
        selectPlaceholderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    PoiDescriptor poiDescriptor = HandlerFilePlaceholder.recoveryPlaceholder(requireContext(),
                            (String) parentView.getItemAtPosition(position));

                    latitudeEditText.setText(String.valueOf(poiDescriptor.getLat()));
                    longitudeEditText.setText(String.valueOf(poiDescriptor.getLng()));
                } else {
                    latitudeEditText.setText(String.valueOf(settingsData.getLatCenter()));
                    longitudeEditText.setText(String.valueOf(settingsData.getLngCenter()));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // switch between forward and reverse geocoding
        ViewSwitcher viewSwitcherCoordinates = view.findViewById(R.id.viewSwitcherCoordinates);

        typeCoordinateSwitch = view.findViewById(R.id.typeCoordinateSwitch);
        typeCoordinateSwitch.setChecked(true);

        typeCoordinateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewSwitcherCoordinates.setDisplayedChild(0);
            } else {
                viewSwitcherCoordinates.setDisplayedChild(1);
            }
        });


        setCoordinate();
        setMapType(view, settingsData.getTypeMap());
        setColorPlaceholder(view, settingsData.getColorPlaceholder());

        //change type of map
        Spinner typeMapSpinner = view.findViewById(R.id.TypeMapSpinner);
        typeMapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setMapType(view, typeMapSpinner.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //change color of placeholder
        Spinner ColorPlaceholderSpinner = view.findViewById(R.id.ColorPlaceholderSpinner);
        ColorPlaceholderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setColorPlaceholder(view, ColorPlaceholderSpinner.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //button to write the changes in the file
        Button writeFileButton = view.findViewById(R.id.saveDatesButton);
        writeFileButton.setOnClickListener(this::writeNewSettings);

        return view;
    }

    private void setCoordinate() {
        latitudeEditText.setText(String.valueOf(settingsData.getLatCenter()));
        longitudeEditText.setText(String.valueOf(settingsData.getLngCenter()));
    }

    private void setMapType(View view, String typeMapFile) {

        Drawable drawable;
        ImageView imageView = view.findViewById(R.id.TypeMapImageView);
        Spinner typeMapSpinner = view.findViewById(R.id.TypeMapSpinner);

        switch (typeMapFile) {
            case "Ibrida":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.map_hybrid);
                typeMapSpinner.setSelection(3);
                settingsData.setTypeMap("Ibrida");
                break;
            case "Satellite":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.map_satellite);
                typeMapSpinner.setSelection(1);
                settingsData.setTypeMap("Satellite");
                break;
            case "Terrestre":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.map_terrain);
                typeMapSpinner.setSelection(2);
                settingsData.setTypeMap("Terrestre");
                break;
            default:  //case "Normale"
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.map_normal);
                typeMapSpinner.setSelection(0);
                settingsData.setTypeMap("Normale");
                break;
        }
        imageView.setImageDrawable(drawable);
    }

    private void setColorPlaceholder(View view, String colorPlaceholderFile){

        Drawable drawable;
        ImageView imageView = view.findViewById(R.id.ColorPlaceholderImageView);
        Spinner ColorPlaceholderSpinner = view.findViewById(R.id.ColorPlaceholderSpinner);

        switch (colorPlaceholderFile) {
            case "Verde":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_place_green);
                ColorPlaceholderSpinner.setSelection(1);
                settingsData.setColorPlaceholder("Verde");
                break;
            case "Arancione":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_place_orange);
                ColorPlaceholderSpinner.setSelection(2);
                settingsData.setColorPlaceholder("Arancione");
                break;
            case "Rosa":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_place_pink);
                ColorPlaceholderSpinner.setSelection(3);
                settingsData.setColorPlaceholder("Rosa");
                break;
            case "Viola":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_place_purple);
                ColorPlaceholderSpinner.setSelection(4);
                settingsData.setColorPlaceholder("Viola");
                break;
            case "Rosso":
                ColorPlaceholderSpinner.setSelection(5);
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_place_red);
                settingsData.setColorPlaceholder("Rosso");
                break;
            case "Giallo":
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_place_yellow);
                ColorPlaceholderSpinner.setSelection(6);
                settingsData.setColorPlaceholder("Giallo");
                break;
            default:  //case "Blu"
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_place_blue);
                ColorPlaceholderSpinner.setSelection(0);
                settingsData.setColorPlaceholder("Blu");
                break;
        }
        imageView.setImageDrawable(drawable);
    }

    private void writeNewSettings(View view){
        settingsData.setLatCenter(Double.parseDouble(latitudeEditText.getText().toString()));
        settingsData.setLngCenter(Double.parseDouble(longitudeEditText.getText().toString()));


        HandlerFile.clearFile(requireContext(), getString(R.string.FILE_SETTINGS));
        HandlerFile.writeFile(requireContext(), getString(R.string.FILE_SETTINGS),  settingsData.toString());

        ((MainActivity) requireActivity()).replaceFragmentWithSettingsFragment();
    }


}