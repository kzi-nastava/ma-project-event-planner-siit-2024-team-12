package com.example.eventplanner.fragments.servicecreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.service.ServiceEditActivity;
import com.example.eventplanner.activities.service.ServiceSolutionService;
import com.example.eventplanner.adapters.solutionservice.ServiceCardAdapter;
import com.example.eventplanner.dto.solutionservice.GetServiceDTO;
import com.example.eventplanner.utils.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductProviderServices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductProviderServices extends Fragment {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private RecyclerView servicesRecyclerView;
    private ServiceCardAdapter serviceCardAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductProviderServices() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a.java new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductProviderServices.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductProviderServices newInstance(String param1, String param2) {
        ProductProviderServices fragment = new ProductProviderServices();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Obradi rezultat ovde ako je potrebno
                        Intent data = result.getData();
                        // Na primer, možeš pročitati podatke iz Intent-a.java ovde
                    }
                }
        );
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_product_provider_services, container, false);
//        AppCompatButton serviceEditButton = view.findViewById(R.id.service_details_button);
//        serviceEditButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), ServiceEditActivity.class);
//            activityResultLauncher.launch(intent);
//        });
//        return view;
//    }
@Override
public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_product_provider_services, container, false);

    servicesRecyclerView = view.findViewById(R.id.services_horizontal_recycler_view);

    // Postavljanje LayoutManager-a za horizontalni prikaz
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    servicesRecyclerView.setLayoutManager(layoutManager);

    // Inicijalizacija adaptera sa praznom listom
    serviceCardAdapter = new ServiceCardAdapter(new ArrayList<>());
    servicesRecyclerView.setAdapter(serviceCardAdapter);

//    serviceCardAdapter.setOnItemClickListener(service -> {
//        Intent intent = new Intent(getActivity(), ServiceEditActivity.class);
//        // You should pass the service ID or full DTO to the new activity.
//        // Make sure your GetServiceDTO class is Serializable or Parcelable.
//        intent.putExtra("serviceId", service.getId());
//        // or
//        // intent.putExtra("serviceDTO", service);
//        activityResultLauncher.launch(intent);
//    });

    // Pozivanje metode za dohvaćanje podataka sa servera
    fetchProvidedServices();

    return view;
}

    private void fetchProvidedServices() {
        // Pretpostavka: auth token i email su dostupni negde u vašem kodu.
        // Na primer, iz SharedPreferences ili Singleton klase
        String auth = ClientUtils.getAuthorization(getContext());

        // Kreiranje Retrofit poziva
        Call<List<GetServiceDTO>> call = ClientUtils.serviceSolutionService.getProvidedServices(auth);

        call.enqueue(new Callback<List<GetServiceDTO>>() {
            @Override
            public void onResponse(Call<List<GetServiceDTO>> call, Response<List<GetServiceDTO>> response) {
                if (response.isSuccessful()) {
                    List<GetServiceDTO> services = response.body();
                    if (services != null && !services.isEmpty()) {
                        // Uspešno učitavanje podataka, ažurirajte adapter
                        serviceCardAdapter.updateServices(services);
                        Log.d("API_CALL", "Successfully loaded " + services.size() + " services.");
                    } else if (response.code() == 204) {
                        // Nema sadržaja (204 No Content), lista je prazna
                        serviceCardAdapter.updateServices(new ArrayList<>());
                        Log.d("API_CALL", "No services found (204 No Content).");
                        Toast.makeText(getContext(), "Nema dostupnih usluga.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Greška, telo odgovora je prazno
                        Toast.makeText(getContext(), "Greška pri učitavanju usluga.", Toast.LENGTH_SHORT).show();
                        Log.e("API_CALL", "Error: Body is null or empty, code: " + response.code());
                    }
                } else {
                    // Neuspešan odgovor servera (npr. 401, 403, 500)
                    Toast.makeText(getContext(), "Greška: " + response.message() + " (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    Log.e("API_CALL", "Unsuccessful response from server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GetServiceDTO>> call, Throwable t) {
                // Greška na mreži (npr. nema interneta)
                Toast.makeText(getContext(), "Greška na mreži: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_CALL", "Network failure: " + t.getMessage());
            }
        });
    }


}