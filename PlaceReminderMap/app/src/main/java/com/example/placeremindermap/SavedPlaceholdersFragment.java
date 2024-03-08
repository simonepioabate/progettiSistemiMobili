package com.example.placeremindermap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedPlaceholdersFragment#} factory method to
 * create an instance of this fragment.
 */
public class SavedPlaceholdersFragment extends Fragment {

    List<PoiDescriptor> poiDescriptorList = new ArrayList<>();

    Button addButton;
    public SavedPlaceholdersFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SavedPlaceholdersFragment.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saved_placeholders, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // Create a LinearLayoutManager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);


        poiDescriptorList.addAll(HandlerFilePlaceholder.readFile(requireContext()));

        // Create an adapter for the RecyclerView to display the list of placeholders
        AdapterSavedPlaceholdersCard adapter = new AdapterSavedPlaceholdersCard(poiDescriptorList,
                requireContext(), requireActivity(), requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adapter);


        addButton = view.findViewById(R.id.button);
        addButton.setOnClickListener(this::addPlaceholderReverseGeocoding);


        return view;
    }

    private void addPlaceholderReverseGeocoding(View view) {
        AddOrEditPlaceholderReverseGeocodingDialogFragment dialogFragment =
                new AddOrEditPlaceholderReverseGeocodingDialogFragment();

        dialogFragment.show(requireActivity().getSupportFragmentManager(), "SavedPlaceholdersFragment");
    }

}