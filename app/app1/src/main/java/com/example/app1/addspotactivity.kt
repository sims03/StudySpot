package com.example.app1

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddSpotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)

        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveSpot() }
    }

    private fun saveSpot() {
        val name    = findViewById<EditText>(R.id.etName).text.toString().trim()
        val address = findViewById<EditText>(R.id.etAddress).text.toString().trim()
        val lat     = findViewById<EditText>(R.id.etLatitude).text.toString().trim()
        val lng     = findViewById<EditText>(R.id.etLongitude).text.toString().trim()
        val email   = findViewById<EditText>(R.id.etEmail).text.toString().trim()
        val phone   = findViewById<EditText>(R.id.etPhone).text.toString().trim()
        val website = findViewById<EditText>(R.id.etWebsite).text.toString().trim()

        val noise = when (findViewById<RadioGroup>(R.id.rgNoise).checkedRadioButtonId) {
            R.id.rbQuiet  -> "Тивко"
            R.id.rbLoud   -> "Бучно"
            else          -> "Средно"
        }

        val hasWifi    = findViewById<CheckBox>(R.id.cbWifi).isChecked
        val hasOutlets = findViewById<CheckBox>(R.id.cbOutlets).isChecked
        val hasCoffee  = findViewById<CheckBox>(R.id.cbCoffee).isChecked
        val hasFood    = findViewById<CheckBox>(R.id.cbFood).isChecked
        val hasParking = findViewById<CheckBox>(R.id.cbParking).isChecked

        val cbIndustry      = findViewById<CheckBox>(R.id.cbIndustry).isChecked
        val cbEntertainment = findViewById<CheckBox>(R.id.cbEntertainment).isChecked
        val cbEducation     = findViewById<CheckBox>(R.id.cbEducation).isChecked
        val cbServices      = findViewById<CheckBox>(R.id.cbServices).isChecked

        if (name.isEmpty()) {
            Toast.makeText(this, "⚠️ Внеси назив!", Toast.LENGTH_SHORT).show()
            return
        }
        if (address.isEmpty()) {
            Toast.makeText(this, "⚠️ Внеси адреса!", Toast.LENGTH_SHORT).show()
            return
        }
        if (!cbIndustry && !cbEntertainment && !cbEducation && !cbServices) {
            Toast.makeText(this, "⚠️ Избери барем една категорија!", Toast.LENGTH_SHORT).show()
            return
        }

        val categories = mutableListOf<String>()
        if (cbIndustry)      categories.add("Индустрија")
        if (cbEntertainment) categories.add("Забава")
        if (cbEducation)     categories.add("Едукација")
        if (cbServices)      categories.add("Сервиси")

        val spot = SpotEntity(
            name       = name,
            address    = address,
            latitude   = lat.toDoubleOrNull() ?: 0.0,
            longitude  = lng.toDoubleOrNull() ?: 0.0,
            email      = email,
            phone      = phone,
            website    = website,
            categories = categories.joinToString(","),
            noise      = noise,
            hasWifi    = hasWifi,
            hasOutlets = hasOutlets,
            hasCoffee  = hasCoffee,
            hasFood    = hasFood,
            hasParking = hasParking
        )

        SpotDatabase.get(this).spotDao().insert(spot)
        Toast.makeText(this, "✅ $name е зачувана!", Toast.LENGTH_LONG).show()
        finish()
    }
}