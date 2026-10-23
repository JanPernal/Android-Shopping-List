package com.example.shoppinglist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("list", MODE_PRIVATE);

        // przycisk "Dodaj" jest tylko w widoku pionowym
        Button btnAdd = findViewById(R.id.btnAdd);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> addItem());
        }

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            showList();
        });

        showList();
    }

    void addItem() {
        EditText etName = findViewById(R.id.etName);
        EditText etQty = findViewById(R.id.etQty);
        EditText etUnit = findViewById(R.id.etUnit);
        EditText etPrice = findViewById(R.id.etPrice);

        String name = etName.getText().toString();
        String qty = etQty.getText().toString();
        String unit = etUnit.getText().toString();
        String price = etPrice.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Podaj nazwę");
            return;
        }
        if (qty.isEmpty()) qty = "1";
        if (unit.isEmpty()) unit = "szt.";
        if (price.isEmpty()) price = "0";

        double sum = Double.parseDouble(qty.replace(",", ".")) * Double.parseDouble(price.replace(",", "."));
        String line = name + " - " + qty + " " + unit + " x " + price + " zł = " + String.format("%.2f", sum) + " zł";

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("items", prefs.getString("items", "") + line + "\n");
        editor.putFloat("total", prefs.getFloat("total", 0) + (float) sum);
        if (!prefs.contains("date")) {
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
            editor.putString("date", "Lista utworzona: " + date);
        }
        editor.apply();

        etName.setText("");
        etQty.setText("");
        etUnit.setText("");
        etPrice.setText("");
        showList();
    }

    void showList() {
        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText(prefs.getString("date", "Lista jest pusta"));

        // lista i suma są tylko w widoku poziomym
        TextView tvList = findViewById(R.id.tvList);
        TextView tvTotal = findViewById(R.id.tvTotal);
        if (tvList != null) {
            tvList.setText(prefs.getString("items", ""));
            tvTotal.setText("Razem: " + String.format("%.2f", prefs.getFloat("total", 0)) + " zł");
        }
    }
}
