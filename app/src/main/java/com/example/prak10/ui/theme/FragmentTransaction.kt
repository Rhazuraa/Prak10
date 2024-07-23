package com.example.prak10.ui.theme

import android.widget.Toast
import com.example.prak10.TransaksiAdapter

companion object {
    lateinit var ryTransaksi: RecyclerView
    lateinit var txtOrder: TextView
    lateinit var txtTax: TextView
    lateinit var txtTotal: TextView
    lateinit var buttonPay: Button
}

fun displayData() {
    // Set layout manager for RecyclerView
    rvTransaksi.layoutManager = LinearLayoutManager(activity)

    // Set adapter for RecyclerView
    rvTransaksi.adapter = TransaksiAdapter()

    // Set values for TextViews
    val harga = TransaksiAdapter.harga

    txtOrder.text = harga.toString()
    txtTax.text = (harga * 0.10).toString()
    txtTotal.text = (harga * 1.10).toString()
}

override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_transaction, container, false)

    // Initialize UI components
    ryTransaksi = view.findViewById(R.id.recyclerTransaksi)
    txtOrder = view.findViewById(R.id.textTotalOrder)
    txtTax = view.findViewById(R.id.textTax)
    txtTotal = view.findViewById(R.id.textTotalPrice)
    buttonPay = view.findViewById(R.id.buttonPayNow)

    // Call displayData() method
    displayData()

    // Set onClickListener for buttonPay
    buttonPay.setOnClickListener {
        val dbHelper = DatabaseHelper(requireContext())
        dbHelper.addTransaction()

        activity?.let { activityContext ->
            val intent = Intent(activityContext, PaymentActivity::class.java)
            startActivity(intent)
        }
    }

    return view
}

inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textId: TextView = view.findViewById(R.id.textIdMenu)
    val textNama: TextView = view.findViewById(R.id.textNamaMenu)
    val textHarga: TextView = view.findViewById(R.id.textHargaMenu)
    val imageMenu: ImageView = view.findViewById(R.id.imageMenu)
    val buttonShop: Button = view.findViewById(R.id.buttonTambah)
    val context = view.context

    init {
        // Any additional initialization code can go here if needed
    }
}


buttonShop.setOnClickListener {
    // Get current item count
    TransaksiAdapter.jumlah = TransaksiAdapter.listId.size
    val jumlahData = TransaksiAdapter.jumlah

    if (jumlahData == 0) {
        // Add new item to the list
        TransaksiAdapter.listId.add(textId.text.toString().toInt())
        TransaksiAdapter.listNama.add(textNama.text.toString())
        TransaksiAdapter.listHarga.add(textHarga.text.toString().toInt())
        TransaksiAdapter.listFoto.add(
            imageMenu.drawable.toBitmap(
                width = 80,
                height = 80,
                config = null
            )
        )
        TransaksiAdapter.listJumlah.add(1)
        TransaksiAdapter.harga += textHarga.text.toString().toInt()

        Toast.makeText(v.context, "Purchase Order Success", Toast.LENGTH_SHORT).show()
    } else {
        // Check if the item is already in the list
        val cek = TransaksiAdapter.listId.find { data ->
            textId.text.toString().toInt() == data
        }

        if (cek == null) {
            // Add new item to the list
            TransaksiAdapter.lisAtId.add(textId.text.toString().toInt())
            TransaksiAdapter.listNama.add(textNama.text.toString())
            TransaksiAdapter.listHarga.add(textHarga.text.toString().toInt())
            TransaksiAdapter.listFoto.add(
                imageMenu.drawable.toBitmap(
                    width = 80,
                    height = 80,
                    config = null
                )
            )
            TransaksiAdapter.listJumlah.add(1)
            TransaksiAdapter.harga += textHarga.text.toString().toInt()

            Toast.makeText(v.context, "Purchase Order Success", Toast.LENGTH_SHORT).show()
        } else {
            // Update existing item
            val index: Int = TransaksiAdapter.listId.indexOf(textId.text.toString().toInt())
            val qty: Int = TransaksiAdapter.listJumlah[index] + 1
            TransaksiAdapter.listJumlah[index] = qty
            TransaksiAdapter.harga += TransaksiAdapter.listHarga[index]

            Toast.makeText(v.context, "Purchase Order Success", Toast.LENGTH_SHORT).show()
        }
    }
}

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Initialize UI components
        val txtTotal: TextView = findViewById(R.id.textViewTotalPurchase)
        val txtChange: TextView = findViewById(R.id.textViewChange)
        val txtCash: EditText = findViewById(R.id.editTextCash)
        val btnFinish: Button = findViewById(R.id.buttonFinish)

        // Calculate total price including tax
        val totalPrice = TransaksiAdapter.harga + (TransaksiAdapter.harga * 0.10)
        txtTotal.text = totalPrice.toString()

        // Set initial change to 0
        txtChange.text = "0"

        // Handle finish button click
        btnFinish.setOnClickListener {
            val cashAmount = txtCash.text.toString().toDoubleOrNull() ?: 0.0
            val totalAmount = txtTotal.text.toString().toDoubleOrNull() ?: 0.0
            val change = cashAmount - totalAmount

            // Update change TextView
            txtChange.text = change.toString()

            // Clear transaction lists and reset totals
            TransaksiAdapter.listId.clear()
            TransaksiAdapter.listNama.clear()
            TransaksiAdapter.listHarga.clear()
            TransaksiAdapter.listJumlah.clear()
            TransaksiAdapter.listFoto.clear()
            TransaksiAdapter.harga = 0
            TransaksiAdapter.jumlah = 0

            // Navigate back to MainActivity
            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
        }
    }
}

