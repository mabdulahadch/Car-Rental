package com.example.carrental.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.carrental.R;
import com.example.carrental.databinding.LayoutFilterBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    public interface FilterListener {
        void onFiltersApplied(String category, float minPrice, float maxPrice,
                              String color, int seats, String fuelType);
    }

    private LayoutFilterBottomSheetBinding binding;
    private FilterListener listener;

    Typeface urbanistRegular;

    // Filter state
    private String selectedCategory = "All";
    private float minPrice = 0;
    private float maxPrice = 100000;
    private String selectedColor = null;
    private int selectedSeats = -1;
    private String selectedFuelType = null;

    // Color data
    private static final String[][] COLORS = {
            {"White", "#FFFFFF"},
            {"Gray", "#9CA3AF"},
            {"Blue", "#3B82F6"},
            {"Black", "#1A1C1E"},
            {"Red", "#EF4444"}
    };

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    public void setInitialState(String category, float minPrice, float maxPrice,
                                String color, int seats, String fuelType) {
        this.selectedCategory = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.selectedColor = color;
        this.selectedSeats = seats;
        this.selectedFuelType = fuelType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            BottomSheetDialog bsd = (BottomSheetDialog) d;
            View bottomSheet = bsd.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
//                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                behavior.setSkipCollapsed(false);
//                bottomSheet.setBackgroundResource(android.R.color.transparent);

                // Set max height to 85% of screen
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
                params.height = (int) (screenHeight * 0.75);
                bottomSheet.setLayoutParams(params);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFonts();
        setupCloseButton();
        setupCarTypeChips();
        setupPriceRange();
        // setupHistogram();
        setupColorSwatches();
        setupSeatChips();
        setupFuelTypeChips();
        setupFooterButtons();
        updateClearAllState();
    }


    private void initFonts() {
        urbanistRegular = ResourcesCompat.getFont(requireContext(), R.font.urbanist_regular);
    }

    private void setupCloseButton() {
        binding.btnCloseFilter.setOnClickListener(v -> dismiss());
    }

    // ─── Car Type Chips ───────────────────────────────────────────

    private void setupCarTypeChips() {
        TextView[] chips = {binding.chipAllCars, binding.chipSuv, binding.chipSedan, binding.chipHatchback};
        String[] values = {"All", "SUV", "Sedan", "Hatchback"};

        for (int i = 0; i < chips.length; i++) {
            int idx = i;
            chips[i].setOnClickListener(v -> {
                selectedCategory = values[idx];
                updateCarTypeChips(chips, idx);
                updateClearAllState();
            });
        }
        
        int initialIdx = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(selectedCategory)) {
                initialIdx = i;
                break;
            }
        }
        updateCarTypeChips(chips, initialIdx);
    }

    private void updateCarTypeChips(TextView[] chips, int selectedIdx) {
        for (int i = 0; i < chips.length; i++) {
            if (i == selectedIdx) {
                chips[i].setBackgroundResource(R.drawable.bg_chip_selected);
                chips[i].setTextColor(Color.WHITE);
                chips[i].setTypeface(urbanistRegular);
            } else {
                chips[i].setBackgroundResource(R.drawable.bg_chip_unselected);
                chips[i].setTextColor(getResources().getColor(R.color.onBackground, null));
                chips[i].setTypeface(urbanistRegular);
            }
        }
    }

    // ─── Price Range ──────────────────────────────────────────────

    private void setupPriceRange() {
        binding.rangeSliderPrice.setValues(minPrice, maxPrice);
        binding.rangeSliderPrice.setStepSize(1000);

        updatePriceLabels(minPrice, maxPrice);

        binding.rangeSliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minPrice = values.get(0);
            maxPrice = values.get(1);
            updatePriceLabels(minPrice, maxPrice);
            updateClearAllState();
        });
    }

    private void updatePriceLabels(float min, float max) {
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        binding.tvMinPrice.setText("PKR " + fmt.format((int) min));
        String maxText = max >= 100000 ? "PKR " + fmt.format((int) max) + "+" : "PKR " + fmt.format((int) max);
        binding.tvMaxPrice.setText(maxText);
    }

    // ─── Histogram ────────────────────────────────────────────────

    // private void setupHistogram() {
    //     binding.llHistogram.removeAllViews();
    //     Random random = new Random(42); // Fixed seed for consistent look
    //     int barCount = 30;

    //     for (int i = 0; i < barCount; i++) {
    //         View bar = new View(requireContext());

    //         // Generate bar height with a bell-curve-like distribution
    //         float normalizedPos = (float) i / barCount;
    //         float baseFactor = (float) (Math.sin(normalizedPos * Math.PI) * 0.7 + 0.3);
    //         float height = baseFactor * (0.4f + random.nextFloat() * 0.6f);

    //         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    //                 0, (int) (80 * getResources().getDisplayMetrics().density * height));
    //         params.weight = 1;
    //         params.setMargins(1, 0, 1, 0);

    //         GradientDrawable bg = new GradientDrawable();
    //         bg.setShape(GradientDrawable.RECTANGLE);
    //         bg.setCornerRadii(new float[]{4, 4, 4, 4, 0, 0, 0, 0}); // top corners rounded
    //         bg.setColor(Color.parseColor("#1A1C1E"));

    //         bar.setBackground(bg);
    //         bar.setLayoutParams(params);
    //         binding.llHistogram.addView(bar);
    //     }
    // }

    // ─── Color Swatches ───────────────────────────────────────────

    private void setupColorSwatches() {
        binding.llColors.removeAllViews();

        for (String[] colorData : COLORS) {
            String name = colorData[0];
            String hex = colorData[1];

            // Container for swatch + label
            LinearLayout container = new LinearLayout(requireContext());
            container.setOrientation(LinearLayout.VERTICAL);
            container.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT);
            containerParams.weight = 1;
            container.setLayoutParams(containerParams);

            // Outer ring (selection indicator)
            android.widget.FrameLayout ringFrame = new android.widget.FrameLayout(requireContext());
            int ringSize = (int) (40 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams ringParams = new LinearLayout.LayoutParams(ringSize, ringSize);
            ringParams.gravity = android.view.Gravity.CENTER;
            ringFrame.setLayoutParams(ringParams);

            // Inner color circle
            View swatch = new View(requireContext());
            int swatchSize = (int) (30 * getResources().getDisplayMetrics().density);
            android.widget.FrameLayout.LayoutParams swatchParams =
                    new android.widget.FrameLayout.LayoutParams(swatchSize, swatchSize);
            swatchParams.gravity = android.view.Gravity.CENTER;
            swatch.setLayoutParams(swatchParams);

            GradientDrawable swatchBg = new GradientDrawable();
            swatchBg.setShape(GradientDrawable.OVAL);
            swatchBg.setColor(Color.parseColor(hex));
            if (name.equals("White")) {
                swatchBg.setStroke((int) (1 * getResources().getDisplayMetrics().density), Color.parseColor("#E5E7EB"));
            }
            swatch.setBackground(swatchBg);

            ringFrame.addView(swatch);

            // Label
            TextView label = new TextView(requireContext());
            label.setText(name);
            label.setTextSize(12);
            label.setTextColor(getResources().getColor(R.color.gray_text, null));
            label.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            labelParams.topMargin = (int) (6 * getResources().getDisplayMetrics().density);
            label.setLayoutParams(labelParams);

            container.addView(ringFrame);
            container.addView(label);

            container.setOnClickListener(v -> {
                if (name.equals(selectedColor)) {
                    selectedColor = null;
                } else {
                    selectedColor = name;
                }
                refreshColorSwatches();
                updateClearAllState();
            });

            binding.llColors.addView(container);
        }
        refreshColorSwatches();
    }

    private void refreshColorSwatches() {
        for (int i = 0; i < binding.llColors.getChildCount(); i++) {
            LinearLayout container = (LinearLayout) binding.llColors.getChildAt(i);
            android.widget.FrameLayout ringFrame = (android.widget.FrameLayout) container.getChildAt(0);
            String name = COLORS[i][0];

            if (name.equals(selectedColor)) {
                ringFrame.setBackgroundResource(R.drawable.bg_color_swatch_ring);
            } else {
                ringFrame.setBackground(null);
            }
        }
    }

    // ─── Seat Chips ───────────────────────────────────────────────

    private void setupSeatChips() {
        TextView[] chips = {binding.chipSeat2, binding.chipSeat4, binding.chipSeat5, binding.chipSeat7, binding.chipSeat8};
        int[] values = {2, 4, 5, 7, 8};

        for (int i = 0; i < chips.length; i++) {
            int idx = i;
            chips[i].setOnClickListener(v -> {
                if (selectedSeats == values[idx]) {
                    selectedSeats = -1; // Deselect
                } else {
                    selectedSeats = values[idx];
                }
                updateSeatChips(chips, values);
                updateClearAllState();
            });
        }
        updateSeatChips(chips, values);
    }

    private void updateSeatChips(TextView[] chips, int[] values) {
        for (int i = 0; i < chips.length; i++) {
            if (values[i] == selectedSeats) {
                chips[i].setBackgroundResource(R.drawable.bg_chip_selected);
                chips[i].setTextColor(Color.WHITE);
                chips[i].setTypeface(urbanistRegular);
            } else {
                chips[i].setBackgroundResource(R.drawable.bg_chip_unselected);
                chips[i].setTextColor(getResources().getColor(R.color.onBackground, null));
                chips[i].setTypeface(urbanistRegular);
            }
        }
    }

    // ─── Fuel Type Chips ──────────────────────────────────────────

    private void setupFuelTypeChips() {
        TextView[] chips = {binding.chipFuelElectric, binding.chipFuelPetrol,
                binding.chipFuelDiesel, binding.chipFuelHybrid};
        String[] values = {"Electric", "Petrol", "Diesel", "Hybrid"};

        for (int i = 0; i < chips.length; i++) {
            int idx = i;
            chips[i].setOnClickListener(v -> {
                if (values[idx].equals(selectedFuelType)) {
                    selectedFuelType = null;
                } else {
                    selectedFuelType = values[idx];
                }
                updateFuelChips(chips, values);
                updateClearAllState();
            });
        }
        updateFuelChips(chips, values);
    }

    private void updateFuelChips(TextView[] chips, String[] values) {
        for (int i = 0; i < chips.length; i++) {
            if (values[i].equals(selectedFuelType)) {
                chips[i].setBackgroundResource(R.drawable.bg_chip_selected);
                chips[i].setTextColor(Color.WHITE);
                chips[i].setTypeface(urbanistRegular);
            } else {
                chips[i].setBackgroundResource(R.drawable.bg_chip_unselected);
                chips[i].setTextColor(getResources().getColor(R.color.onBackground, null));
                chips[i].setTypeface(urbanistRegular);
            }
        }
    }

    // ─── Footer Buttons ───────────────────────────────────────────

    private void setupFooterButtons() {
        binding.btnClearAll.setOnClickListener(v -> {
            clearAllFilters();
        });

        binding.btnShowCars.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFiltersApplied(selectedCategory, minPrice, maxPrice,
                        selectedColor, selectedSeats, selectedFuelType);
            }
            dismiss();
        });
    }

    private void clearAllFilters() {
        selectedCategory = "All";
        minPrice = 0;
        maxPrice = 100000;
        selectedColor = null;
        selectedSeats = -1;
        selectedFuelType = null;

        // Reset UI
        TextView[] typeChips = {binding.chipAllCars, binding.chipSuv, binding.chipSedan, binding.chipHatchback};
        updateCarTypeChips(typeChips, 0);

        binding.rangeSliderPrice.setValues(0f, 100000f);
        updatePriceLabels(0, 100000);

        refreshColorSwatches();

        TextView[] seatChips = {binding.chipSeat2, binding.chipSeat4, binding.chipSeat5, binding.chipSeat7, binding.chipSeat8};
        updateSeatChips(seatChips, new int[]{2, 4, 5, 7, 8});

        TextView[] fuelChips = {binding.chipFuelElectric, binding.chipFuelPetrol,
                binding.chipFuelDiesel, binding.chipFuelHybrid};
        updateFuelChips(fuelChips, new String[]{"Electric", "Petrol", "Diesel", "Hybrid"});
        
        updateClearAllState();
    }

    private void updateClearAllState() {
        boolean isFilterSelected = !selectedCategory.equals("All") ||
                minPrice > 0 || maxPrice < 100000 ||
                selectedColor != null ||
                selectedSeats != -1 ||
                selectedFuelType != null;

        if (isFilterSelected) {
            String text = "Clear All \u2022";
            android.text.SpannableString spannable = new android.text.SpannableString(text);
            spannable.setSpan(new android.text.style.ForegroundColorSpan(getResources().getColor(R.color.primary, null)), 
                text.length() - 1, text.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.RelativeSizeSpan(1.5f), 
                text.length() - 1, text.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.btnClearAll.setText(spannable);
        } else {
            binding.btnClearAll.setText("Clear All");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
