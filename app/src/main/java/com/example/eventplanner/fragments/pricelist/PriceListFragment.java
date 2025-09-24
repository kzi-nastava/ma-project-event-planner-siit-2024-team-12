package com.example.eventplanner.fragments.pricelist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.pricelist.PriceListAdapter;
import com.example.eventplanner.dto.pricelist.GetPriceListItemDTO;
import com.example.eventplanner.dto.pricelist.GetPriceListSolutionDTO;
import com.example.eventplanner.viewmodels.PriceListViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import android.graphics.pdf.PdfDocument;

public class PriceListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;
    private PriceListViewModel viewModel;
    private TextView tvTitle;
    private TextView tvValidFrom;
    private RecyclerView rvPriceListItems;
    private PriceListAdapter adapter;
    private ImageButton btnPdf;

    public PriceListFragment() {
        // Obavezni prazan konstruktor
    }

    public static PriceListFragment newInstance(String type) {
        PriceListFragment fragment = new PriceListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
        viewModel = new ViewModelProvider(this).get(PriceListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_list, container, false);

        tvTitle = view.findViewById(R.id.tv_title);
        tvValidFrom = view.findViewById(R.id.tv_valid_from);
        rvPriceListItems = view.findViewById(R.id.rv_price_list_items);

        rvPriceListItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PriceListAdapter(new ArrayList<>());
        rvPriceListItems.setAdapter(adapter);

        btnPdf = view.findViewById(R.id.btn_generate_pdf);

        btnPdf.setOnClickListener(v -> checkPermissionAndGeneratePdf());

        adapter.setOnItemActionListener((item, updateDTO, position) -> {
            viewModel.updatePriceListItem(item.getId(), type, updateDTO);
        });

        setupObservers();

        if (type != null) {
            viewModel.fetchPriceList(type);
        }

        return view;
    }

    private void setupObservers() {
        viewModel.getPriceList().observe(getViewLifecycleOwner(), priceListDTO -> {
            if (priceListDTO != null) {
                if(priceListDTO.getPriceListItems().isEmpty()){
                    tvValidFrom.setText("There are currently no items in the price list.");
                    btnPdf.setVisibility(View.GONE);
                }else{
                    tvValidFrom.setText("Valid from: " + priceListDTO.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
                }
                adapter.setItems(priceListDTO.getPriceListItems());
            } else {
                tvValidFrom.setText("No price list available.");
                adapter.setItems(new ArrayList<>());
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdatedItem().observe(getViewLifecycleOwner(), updatedItem -> {
            if (updatedItem != null) {
                List<GetPriceListItemDTO> currentItems = adapter.getItems();
                for (int i = 0; i < currentItems.size(); i++) {
                    if (currentItems.get(i).getId().equals(updatedItem.getId())) {
                        GetPriceListSolutionDTO solution = new GetPriceListSolutionDTO();
                        solution.setPrice(updatedItem.getSolution().getPrice());
                        solution.setDiscount(updatedItem.getSolution().getDiscount());
                        solution.setDescription(currentItems.get(i).getSolution().getDescription());
                        solution.setName(currentItems.get(i).getSolution().getName());
                        currentItems.set(i, new GetPriceListItemDTO(
                                updatedItem.getId(),
                                updatedItem.getDiscountPrice(),
                                solution
                        ));
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
                Toast.makeText(getContext(), "Item updated successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static final int PERMISSION_REQUEST_CODE = 1;

    private void checkPermissionAndGeneratePdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            generatePdf();
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                generatePdf();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePdf();
            } else {
                Toast.makeText(getContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generatePdf() {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(getContext(), "No items to generate.", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument document = new PdfDocument();
        int pageWidth = 595;
        int pageHeight = 842;
        int margin = 50;
        int currentY = margin;

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(36);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint subtitlePaint = new Paint();
        subtitlePaint.setTextSize(16);
        subtitlePaint.setColor(getResources().getColor(android.R.color.darker_gray));

        Paint itemTitlePaint = new Paint();
        itemTitlePaint.setTextSize(20);
        itemTitlePaint.setFakeBoldText(true);
        itemTitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint textPaint = new Paint();
        textPaint.setTextSize(14);

        Paint dividerPaint = new Paint();
        dividerPaint.setColor(getResources().getColor(android.R.color.darker_gray));
        dividerPaint.setStrokeWidth(1);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        canvas.drawText("Price list", margin, currentY, titlePaint);
        currentY += 40;
        canvas.drawText(tvValidFrom.getText().toString(), margin, currentY, subtitlePaint);
        currentY += 30;

        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, dividerPaint);
        currentY += 30;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            GetPriceListItemDTO item = adapter.getItems().get(i);

            int itemHeight = 120;
            if (currentY + itemHeight > pageHeight - margin) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                currentY = margin;
            }

            String itemTitle = (i + 1) + ". " + item.getSolution().getName();
            canvas.drawText(itemTitle, margin, currentY, itemTitlePaint);
            currentY += 24;

            String description = item.getSolution().getDescription();
            canvas.drawText(description, margin + 10, currentY, textPaint);
            currentY += 24;

            double originalPrice = item.getSolution().getPrice();
            double discountPercentage = item.getSolution().getDiscount();
            double finalPrice = originalPrice - (originalPrice * (discountPercentage / 100));

            canvas.drawText(String.format("Price: $%.2f", originalPrice), margin + 10, currentY, textPaint);
            canvas.drawText(String.format("Discount: %.0f%%", discountPercentage), margin + 180, currentY, textPaint);
            currentY += 24;

            String finalPriceText = String.format("Final price: $%.2f", finalPrice);
            canvas.drawText(finalPriceText, margin + 10, currentY, itemTitlePaint);
            currentY += 32;

            if (i < adapter.getItemCount() - 1) {
                canvas.drawLine(margin, currentY, pageWidth - margin, currentY, dividerPaint);
                currentY += 24;
            }
        }

        document.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "price_list.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(getContext(), "Successfully generated PDF!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}