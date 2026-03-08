package com.example.app1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.widget.Toast

data class StudySpot(
    val id: Int,
    val name: String,
    val type: String,
    val typeEmoji: String,
    val noise: String,
    val noiseEmoji: String,
    val rating: Float,
    val reviewCount: Int,
    val description: String,
    val address: String,
    val phone: String,
    val website: String,
    val hasWifi: Boolean,
    val hasOutlets: Boolean,
    val hasCoffee: Boolean,
    val hasFood: Boolean,
    val hasParking: Boolean,
    val latitude: Double,
    val longitude: Double
)

val sampleSpots = listOf(
    StudySpot(1, "Coffee Lab", "Сервиси", "☕", "Средно", "💬", 4.5f, 12,
        "Пријатна атмосфера, брз интернет и добро кафе.",
        "Ул. Македонија 5, Штип", "+389 32 123 456", "www.coffeelab.mk",
        hasWifi = true, hasOutlets = true, hasCoffee = true, hasFood = false, hasParking = false,
        latitude = 41.7459, longitude = 22.1975),
    StudySpot(2, "Градска Библиотека", "Едукација", "📚", "Тивко", "🔇", 4.8f, 34,
        "Одлична тишина и богат фонд.",
        "Ул. Герас Цунев 1, Штип", "+389 32 234 567", "www.biblioteka.stip.mk",
        hasWifi = true, hasOutlets = true, hasCoffee = false, hasFood = false, hasParking = true,
        latitude = 41.7461, longitude = 22.1980),
    StudySpot(3, "Study Cafe", "Сервиси", "☕", "Средно", "💬", 4.3f, 8,
        "Специјализирано кафе за студенти.",
        "Ул. Илинденска 12, Штип", "+389 32 345 678", "www.studycafe.mk",
        hasWifi = true, hasOutlets = true, hasCoffee = true, hasFood = true, hasParking = false,
        latitude = 41.7455, longitude = 22.1965),
    StudySpot(4, "UGD Библиотека", "Едукација", "📚", "Тивко", "🔇", 4.6f, 21,
        "Универзитетска библиотека со современа опрема.",
        "Ул. Крсте Мисирков бб, Штип", "+389 32 456 789", "www.ugd.edu.mk",
        hasWifi = true, hasOutlets = true, hasCoffee = false, hasFood = false, hasParking = true,
        latitude = 41.7470, longitude = 22.2010),
    StudySpot(5, "Hub Štip", "Индустрија", "💼", "Средно", "💬", 4.4f, 6,
        "Современ coworking простор.",
        "Ул. Вардарска 8, Штип", "+389 32 567 890", "www.hubstip.mk",
        hasWifi = true, hasOutlets = true, hasCoffee = true, hasFood = false, hasParking = true,
        latitude = 41.7448, longitude = 22.1955),
    StudySpot(6, "Кино Штип", "Забава", "🎬", "Бучно", "📢", 3.9f, 15,
        "Кино и забавен центар.",
        "Ул. Центар 3, Штип", "+389 32 678 901", "www.kinostip.mk",
        hasWifi = false, hasOutlets = false, hasCoffee = true, hasFood = true, hasParking = true,
        latitude = 41.7465, longitude = 22.1990)
)

class SpotAdapter(
    private var spots: List<StudySpot>,
    private val onClick: (StudySpot) -> Unit
) : RecyclerView.Adapter<SpotAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView        = view.findViewById(R.id.tvName)
        val tvType: TextView        = view.findViewById(R.id.tvType)
        val tvRating: TextView      = view.findViewById(R.id.tvRating)
        val tvNoise: TextView       = view.findViewById(R.id.tvNoise)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvReviews: TextView     = view.findViewById(R.id.tvReviews)
        val tvFeatures: TextView    = view.findViewById(R.id.tvFeatures)
        val tvAddress: TextView     = view.findViewById(R.id.tvAddress)
        val tvPhone: TextView       = view.findViewById(R.id.tvPhone)
        val tvWebsite: TextView     = view.findViewById(R.id.tvWebsite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = spots[position]
        holder.tvName.text        = s.name
        holder.tvType.text        = "${s.typeEmoji} ${s.type}"
        holder.tvRating.text      = "★ ${"%.1f".format(s.rating)}"
        holder.tvNoise.text       = "${s.noiseEmoji} ${s.noise}"
        holder.tvDescription.text = s.description
        holder.tvReviews.text     = "${s.reviewCount} рецензии"
        holder.tvAddress.text     = "📍 ${s.address}"
        holder.tvPhone.text       = "📞 ${s.phone}"
        holder.tvWebsite.text     = "🌐 ${s.website}"
        holder.tvFeatures.text    = buildString {
            if (s.hasWifi)    append("📶 WiFi  ")
            if (s.hasOutlets) append("🔌 Приклучоци  ")
            if (s.hasCoffee)  append("☕ Кафе  ")
            if (s.hasFood)    append("🥐 Храна  ")
            if (s.hasParking) append("🚗 Паркинг")
        }.trim()

        val stars = listOf(
            holder.itemView.findViewById<TextView>(R.id.star1),
            holder.itemView.findViewById<TextView>(R.id.star2),
            holder.itemView.findViewById<TextView>(R.id.star3),
            holder.itemView.findViewById<TextView>(R.id.star4),
            holder.itemView.findViewById<TextView>(R.id.star5)
        )

        fun updateStars(selected: Int) {
            stars.forEachIndexed { i, star ->
                star.text = if (i < selected) "★" else "☆"
            }
        }

        updateStars(s.rating.toInt())

        stars.forEachIndexed { i, star ->
            star.setOnClickListener {
                val newRating = i + 1
                updateStars(newRating)
                if (s.id > 0) {
                    val db = SpotDatabase.get(it.context)
                    val current = db.spotDao().getAll().find { e -> e.id == s.id }
                    if (current != null) {
                        val newCount = current.reviewCount + 1
                        val newAvg = ((current.rating * current.reviewCount) + newRating) / newCount
                        db.spotDao().updateRating(s.id, newAvg, newCount)
                        holder.tvRating.text = "★ ${"%.1f".format(newAvg)}"
                        holder.tvReviews.text = "$newCount рецензии"
                    }
                }
                Toast.makeText(it.context, "Гласавте со $newRating ѕвезди!", Toast.LENGTH_SHORT).show()
            }
        }

        holder.itemView.setOnClickListener { onClick(s) }
    }

    override fun getItemCount() = spots.size

    fun updateList(newSpots: List<StudySpot>) {
        spots = newSpots
        notifyDataSetChanged()
    }
}

class CategoryFragment : Fragment() {

    private lateinit var adapter: SpotAdapter
    private var category = ""

    companion object {
        fun newInstance(category: String): CategoryFragment {
            val f = CategoryFragment()
            f.arguments = Bundle().apply { putString("category", category) }
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("category") ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val searchInput  = view.findViewById<EditText>(R.id.searchInput)
        val tvCount      = view.findViewById<TextView>(R.id.tvCount)

        adapter = SpotAdapter(emptyList()) { spot ->
            (activity as? MainActivity)?.showDetail(spot)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fun applyFilter(query: String = "") {
            val db = SpotDatabase.get(requireContext())
            val roomSpots = db.spotDao().getAll().flatMap { entity ->
                entity.categories.split(",").map { cat -> cat.trim() to entity }
            }.filter { (cat, entity) ->
                cat == category &&
                        (query.isEmpty() || entity.name.lowercase().contains(query.lowercase()))
            }.map { (cat, entity) ->
                val noiseEmoji = when (entity.noise) {
                    "Тивко" -> "🔇"
                    "Бучно" -> "📢"
                    else -> "💬"
                }
                StudySpot(
                    id = entity.id, name = entity.name, type = cat, typeEmoji = "",
                    noise = entity.noise, noiseEmoji = noiseEmoji,
                    rating = entity.rating, reviewCount = entity.reviewCount,
                    description = "", address = entity.address, phone = entity.phone,
                    website = entity.website,
                    hasWifi = entity.hasWifi, hasOutlets = entity.hasOutlets,
                    hasCoffee = entity.hasCoffee, hasFood = entity.hasFood,
                    hasParking = entity.hasParking,
                    latitude = entity.latitude, longitude = entity.longitude
                )
            }

            val sampleFiltered = sampleSpots.filter { s ->
                s.type == category &&
                        (query.isEmpty() || s.name.lowercase().contains(query.lowercase()))
            }

            val allSpots = sampleFiltered + roomSpots
            adapter.updateList(allSpots)
            tvCount.text = "${allSpots.size} места"
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = applyFilter(s.toString())
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        applyFilter()
    }
}

class TabAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    val categories = listOf("Сервиси", "Забава", "Индустрија", "Едукација")
    override fun getItemCount() = categories.size
    override fun createFragment(position: Int) = CategoryFragment.newInstance(categories[position])
}

class MainActivity : AppCompatActivity() {

    private lateinit var detailPanel: View
    private lateinit var mainContent: View
    private lateinit var tvDetailName: TextView
    private lateinit var tvDetailType: TextView
    private lateinit var tvDetailRating: TextView
    private lateinit var tvDetailNoise: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var tvDetailFeatures: TextView
    private lateinit var tvDetailAddress: TextView
    private lateinit var tvDetailPhone: TextView
    private lateinit var tvDetailWebsite: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationPermissionRequest = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val btnAdd = findViewById<TextView>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            startActivity(android.content.Intent(this, AddSpotActivity::class.java))
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val tabAdapter = TabAdapter(this)
        viewPager.adapter = tabAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabAdapter.categories[position]
        }.attach()

        mainContent         = findViewById(R.id.mainContent)
        detailPanel         = findViewById(R.id.detailPanel)
        tvDetailName        = findViewById(R.id.tvDetailName)
        tvDetailType        = findViewById(R.id.tvDetailType)
        tvDetailRating      = findViewById(R.id.tvDetailRating)
        tvDetailNoise       = findViewById(R.id.tvDetailNoise)
        tvDetailDescription = findViewById(R.id.tvDetailDescription)
        tvDetailFeatures    = findViewById(R.id.tvDetailFeatures)
        tvDetailAddress     = findViewById(R.id.tvDetailAddress)
        tvDetailPhone       = findViewById(R.id.tvDetailPhone)
        tvDetailWebsite     = findViewById(R.id.tvDetailWebsite)

        findViewById<Button>(R.id.btnBack).setOnClickListener { showMain() }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (detailPanel.isVisible) showMain() else finish()
            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1002
            )
        }
    }

    fun showDetail(spot: StudySpot) {
        mainContent.isVisible    = false
        detailPanel.isVisible    = true
        tvDetailName.text        = spot.name
        tvDetailType.text        = "${spot.typeEmoji} ${spot.type}"
        tvDetailRating.text      = "★ ${spot.rating}  ·  ${spot.reviewCount} рецензии"
        tvDetailNoise.text       = "${spot.noiseEmoji} Бучава: ${spot.noise}"
        tvDetailDescription.text = spot.description
        tvDetailAddress.text     = "📍 ${spot.address}"
        tvDetailPhone.text       = "📞 ${spot.phone}"
        tvDetailWebsite.text     = "🌐 ${spot.website}"
        tvDetailFeatures.text    = buildString {
            if (spot.hasWifi)    appendLine("📶  WiFi")
            if (spot.hasOutlets) appendLine("🔌  Приклучоци за лаптоп")
            if (spot.hasCoffee)  appendLine("☕  Кафе достапно")
            if (spot.hasFood)    appendLine("🥐  Храна достапна")
            if (spot.hasParking) appendLine("🚗  Паркинг")
        }.trim()

        findViewById<Button>(R.id.btnShowMap).setOnClickListener {
            val uri = android.net.Uri.parse(
                "geo:${spot.latitude},${spot.longitude}?q=${spot.latitude},${spot.longitude}(${spot.name})"
            )
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
            startActivity(mapIntent)
        }
    }

    private fun showMain() {
        detailPanel.isVisible = false
        mainContent.isVisible = true
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequest
            )
        } else {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequest &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L
        ).setMinUpdateIntervalMillis(5000L).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                checkNearbySpots(location)
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        }
    }

    private fun checkNearbySpots(location: Location) {
        val allSpots = sampleSpots + SpotDatabase.get(this).spotDao().getAll().map { entity ->
            StudySpot(
                id = entity.id, name = entity.name, type = "", typeEmoji = "",
                noise = "", noiseEmoji = "", rating = 0f, reviewCount = 0,
                description = "", address = entity.address, phone = "", website = "",
                hasWifi = false, hasOutlets = false, hasCoffee = false, hasFood = false, hasParking = false,
                latitude = entity.latitude, longitude = entity.longitude
            )
        }

        allSpots.forEach { spot ->
            val spotLocation = Location("spot").apply {
                latitude  = spot.latitude
                longitude = spot.longitude
            }
            val distance = location.distanceTo(spotLocation)
            if (distance < 50f) {
                sendNearbyNotification(spot.name)
            }
        }
    }

    private fun sendNearbyNotification(spotName: String) {
        val channelId = "nearby_spots"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Блиски места",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Нотификации за блиски места"
            }
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("📍 Си блиску до StudySpot!")
            .setContentText("Се наоѓаш во близина на $spotName")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (android.os.Build.VERSION.SDK_INT < 33 ||
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            androidx.core.app.NotificationManagerCompat.from(this)
                .notify(spotName.hashCode(), notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}