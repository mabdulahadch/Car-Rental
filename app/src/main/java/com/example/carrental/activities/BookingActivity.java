package com.example.carrental.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.api.RetrofitClient;
import com.example.carrental.databinding.ActivityBookingBinding;
import com.example.carrental.models.Booking;
import com.example.carrental.models.BookingRequest;
import com.example.carrental.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private SessionManager sessionManager;

    private String carId;
    private String carName;
    private double pricePerDay;
    private String carImage;

    private Calendar pickupCalendar = Calendar.getInstance();
    private Calendar returnCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private boolean pickupSelected = false;
    private boolean returnSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        // Get car data from intent
        carId = getIntent().getStringExtra("CAR_ID");
        carName = getIntent().getStringExtra("CAR_NAME");
        pricePerDay = getIntent().getDoubleExtra("CAR_PRICE", 0);
        carImage = getIntent().getStringExtra("CAR_IMAGE");

        if (carId == null) {
            finish();
            return;
        }

        setupUI();
        setupDatePickers();
        setupListeners();
    }

    private void setupUI() {
        binding.tvBookingCarName.setText(carName != null ? carName : "Car");
        binding.tvBookingCarPrice.setText("PKR " + (int) pricePerDay + "/Day");

        if (carImage != null && !carImage.isEmpty()) {
            Glide.with(this).load(carImage).placeholder(R.drawable.car_image).into(binding.ivBookingCar);
        }

        // Pre-fill user data if logged in
        if (sessionManager.isLoggedIn()) {
            binding.etBookingName.setText(sessionManager.getUserName());
            binding.etBookingEmail.setText(sessionManager.getUserEmail());
        }

        binding.btnBookingBack.setOnClickListener(v -> finish());
    }

    private void setupDatePickers() {
        binding.etPickupDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                pickupCalendar.set(year, month, dayOfMonth);
                binding.etPickupDate.setText(displayFormat.format(pickupCalendar.getTime()));
                pickupSelected = true;
                calculateTotal();
            }, pickupCalendar.get(Calendar.YEAR), pickupCalendar.get(Calendar.MONTH), pickupCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });

        binding.etReturnDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                returnCalendar.set(year, month, dayOfMonth);
                binding.etReturnDate.setText(displayFormat.format(returnCalendar.getTime()));
                returnSelected = true;
                calculateTotal();
            }, returnCalendar.get(Calendar.YEAR), returnCalendar.get(Calendar.MONTH), returnCalendar.get(Calendar.DAY_OF_MONTH));
            // Return date must be after pickup
            if (pickupSelected) {
                dialog.getDatePicker().setMinDate(pickupCalendar.getTimeInMillis());
            } else {
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }
            dialog.show();
        });
    }

    private void setupListeners() {
        binding.switchDriver.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotal());

        binding.btnConfirmBooking.setOnClickListener(v -> submitBooking());
    }

    private void calculateTotal() {
        if (!pickupSelected || !returnSelected) {
            binding.tvTotalAmount.setText("PKR 0");
            return;
        }

        long diffMs = returnCalendar.getTimeInMillis() - pickupCalendar.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(diffMs);
        if (days <= 0) days = 1; // Minimum 1 day

        double total = days * pricePerDay;
        if (binding.switchDriver.isChecked()) {
            total += days * 2000; // PKR 2000/day driver charge
        }

        binding.tvTotalAmount.setText("PKR " + (int) total);
    }

    private void submitBooking() {
        String name = binding.etBookingName.getText().toString().trim();
        String email = binding.etBookingEmail.getText().toString().trim();
        String phone = binding.etBookingPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pickupSelected || !returnSelected) {
            Toast.makeText(this, "Please select pickup and return dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (returnCalendar.before(pickupCalendar)) {
            Toast.makeText(this, "Return date must be after pickup date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine gender
        int selectedGenderId = binding.rgGender.getCheckedRadioButtonId();
        String gender = "Male";
        if (selectedGenderId == R.id.rb_female) {
            gender = "Female";
        }

        // Calculate total
        long diffMs = returnCalendar.getTimeInMillis() - pickupCalendar.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(diffMs);
        if (days <= 0) days = 1;
        double total = days * pricePerDay;
        if (binding.switchDriver.isChecked()) {
            total += days * 2000;
        }

        String userId = sessionManager.getUserId();

        BookingRequest request = new BookingRequest(
                name, email, phone, gender,
                dateFormat.format(pickupCalendar.getTime()),
                dateFormat.format(returnCalendar.getTime()),
                total,
                binding.switchDriver.isChecked(),
                carId,
                userId != null ? userId : "guest"
        );

        binding.btnConfirmBooking.setEnabled(false);
        binding.btnConfirmBooking.setText("Booking...");

        RetrofitClient.getCarApiService().createBooking(request).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                binding.btnConfirmBooking.setEnabled(true);
                binding.btnConfirmBooking.setText("Confirm Booking");
                if (response.isSuccessful()) {
                    Toast.makeText(BookingActivity.this, "Booking confirmed successfully!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(BookingActivity.this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                binding.btnConfirmBooking.setEnabled(true);
                binding.btnConfirmBooking.setText("Confirm Booking");
                Toast.makeText(BookingActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
