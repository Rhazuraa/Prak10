package com.example.prak10

// Table transaksi
private val TABLE_TRANS = "transaksi"

// Columns of the transaksi table
private val COLUMN_ID_TRANS = "idTransaksi"
private val COLUMN_TGL = "tanggal"
private val COLUMN_USER = "user"

// Table detail transaksi
private val TABLE_DET_TRANSACTION = "detailTrans"

// Columns of the detail transaksi table
private val COLUMN_ID_DET_TRX = "idDetailTrx"
private val COLUMN_ID_TRX = "idTransaksi"
private val COLUMN_ID_PESAN = "idMenu"
private val COLUMN_HARGA_PESAN = "harga"
private val COLUMN_JUMLAH = "jumlah"

// Create table transaksi SQL query
private val CREATE_TRANSACTION_TABLE = """
    CREATE TABLE $TABLE_TRANS (
        $COLUMN_ID_TRANS INT PRIMARY KEY,
        $COLUMN_TGL TEXT,
        $COLUMN_USER TEXT
    )
"""

// Drop table transaksi SQL query
private val DROP_TRANSACTION_TABLE = "DROP TABLE IF EXISTS $TABLE_TRANS"

// Create table detail transaksi SQL query
private val CREATE_DET_TRANS_TABLE = """
    CREATE TABLE $TABLE_DET_TRANSACTION (
        $COLUMN_ID_DET_TRX INT PRIMARY KEY,
        $COLUMN_ID_TRX INT,
        $COLUMN_ID_PESAN INT,
        $COLUMN_HARGA_PESAN INT,
        $COLUMN_JUMLAH INT
    )
"""

// Drop table detail transaksi SQL query
private val DROP_DET_TRANS_TABLE = "DROP TABLE IF EXISTS $TABLE_DET_TRANSACTION"

override fun onCreate(db: SQLiteDatabase?) {
    db?.execSQL(CREATE_ACCOUNT_TABLE)
    db?.execSQL(CREATE_MENU_TABLE)
    db?.execSQL(CREATE_TRANSACTION_TABLE)
    db?.execSQL(CREATE_DET_TRANS_TABLE)
    db?.execSQL(INSERT_ACCOUNT_TABLE)
}

override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    db?.execSQL(DROP_ACCOUNT_TABLE)
    db?.execSQL(DROP_MENU_TABLE)
    db?.execSQL(DROP_TRANSACTION_TABLE)
    db?.execSQL(DROP_DET_TRANS_TABLE)
    onCreate(db)
}

@SuppressLint("Range")
fun addTransaction() {
    val dbInsert = this.writableDatabase
    val dbSelect = this.readableDatabase

    // Declare variables
    var lastIdTrans = 0
    var lastIdDetail = 0
    var newIdTrans = 0
    var newIdDetail = 0
    val values = ContentValues()

    // Get last idTransaksi
    val cursorTrans: Cursor = dbSelect.rawQuery(
        sql = "SELECT * FROM $TABLE_TRANS",
        selectionArgs = null
    )

    val cursorDetail: Cursor = dbSelect.rawQuery(
        sql = "SELECT * FROM $TABLE_DET_TRANSACTION",
        selectionArgs = null
    )

    if (cursorTrans.moveToLast()) {
        lastIdTrans = cursorTrans.getInt(0) // To get id, 0 is the column index
    }

    if (cursorDetail.moveToLast()) {
        lastIdDetail = cursorDetail.getInt(0) // To get id, 0 is the column index
    }

    // Set data
    newIdTrans = lastIdTrans + 1
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val tanggal = sdf.format(Date())
    val username = FragmentProfile.email

    // Insert data transaksi
    values.put(COLUMN_ID_TRANS, newIdTrans)
    values.put(COLUMN_TGL, tanggal)
    values.put(COLUMN_USER, username)
    val result = dbInsert.insert(TABLE_TRANS, null, values)

    // Show message
    if (result == -1L) {
        Toast.makeText(context, "Add transaction Failed", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Add transaction Success", Toast.LENGTH_SHORT).show()
        newIdDetail = lastIdDetail + 1

        val values2 = ContentValues()
        var i = 0

        while (i < TransaksiAdapter.listId.size) {
            values2.put(COLUMN_ID_DET_TRX, newIdDetail)
            values2.put(COLUMN_ID_TRX, newIdTrans)
            values2.put(COLUMN_ID_PESAN, TransaksiAdapter.listId[i])
            values2.put(COLUMN_HARGA_PESAN, TransaksiAdapter.listHarga[i])
            values2.put(COLUMN_JUMLAH, TransaksiAdapter.listJumlah[i])

            val result2 = dbInsert.insert(TABLE_DET_TRANSACTION, null, values2)

            if (result2 == -1L) {
                Toast.makeText(context, "Add detail Failed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Add detail Success", Toast.LENGTH_SHORT).show()
            }

            newIdDetail += 1
            i += 1
        }
    }

    dbSelect.close()
    dbInsert.close()
}


class TransaksiAdapter : RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    // ViewHolder class to hold the views
    inner class TransaksiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define views here
    }

    // Provide a reference to the type of views that you are using
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaksi, parent, false)
        return TransaksiViewHolder(view)
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        // Bind data to the views in the ViewHolder
    }

    // Return the size of the dataset
    override fun getItemCount(): Int {
        // Return the number of items in the dataset
        return 0
    }

    companion object {
        val listId: Any
    }
}

companion object {
    var jumlah: Int = 0
    var listId = mutableListOf<Int>()
    var listNama = mutableListOf<String>()
    var listHarga = mutableListOf<Int>()
    var listFoto = mutableListOf<Bitmap>()
    var listJumlah = mutableListOf<Int>()
    var harga: Int = 0
}

inner class TransaksiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val foto: ImageView = view.findViewById(R.id.imageTransaksi)
    val idMenu: TextView = view.findViewById(R.id.textIdTransaksi)
    val namaMenu: TextView = view.findViewById(R.id.textNamaTransaksi)
    val textHarga: TextView = view.findViewById(R.id.textHargaTransaksi)
    val jumlah: TextView = view.findViewById(R.id.textQtyTransaksi)
    val tambahQty: ImageView = view.findViewById(R.id.imageButtonPlus)
    val kurangQty: ImageView = view.findViewById(R.id.imageButtonMinus)
    val buttonHapus: Button = view.findViewById(R.id.buttonHapusTransaksi)
    val context = view.context

    init {
        tambahQty.setOnClickListener {
            val qty: Int = jumlah.text.toString().toInt()
            Toast.makeText(context, jumlah.text.toString(), Toast.LENGTH_SHORT).show()
            jumlah.text = (qty + 1).toString()

            val harga = textHarga.text.toString().toInt()
            FragmentTransaction.txtOrder.text = harga.toString()
            FragmentTransaction.txtTax.text = (harga * 0.18).toString()
            FragmentTransaction.txtTotal.text = (harga * 1.10).toString()
        }

        kurangQty.setOnClickListener {
            val qty: Int = jumlah.text.toString().toInt()
            if (qty > 1) {
                jumlah.text = (qty - 1).toString()

                val harga = textHarga.text.toString().toInt()
                FragmentTransaction.txtOrder.text = harga.toString()
                FragmentTransaction.txtTax.text = (harga * 0.10).toString()
                FragmentTransaction.txtTotal.text = (harga * 1.10).toString()
            }
        }
    }
}

override fun getItemCount(): Int {
    return listId.size
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val cellForRow = layoutInflater.inflate(R.layout.cardview_transaction, parent, false)
    return TransaksiViewHolder(cellForRow)
}

override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
    holder.idMenu.text = listId[position].toString()
    holder.namaMenu.text = listNama[position]
    holder.textHarga.text = listHarga[position].toString()
    holder.jumlah.text = listJumlah[position].toString()
    holder.foto.setImageBitmap(listFoto[position])
}

