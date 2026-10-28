package com.bizpulse.app.data.db
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bizpulse.app.util.*
import java.time.LocalDate
/** Callback runs inside database creation; raw inserts avoid recursively reopening Room. */
object DatabaseSeeder {
    fun seed(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()
        val users = listOf(listOf("Maria Santos","owner","owner123","owner"), listOf("Juan Dela Cruz","cashier","cashier123","cashier"), listOf("Ana Reyes","ana","ana123","cashier"))
        users.forEachIndexed { i, u -> db.execSQL("INSERT INTO users(id,name,username,passwordHash,role,createdAt) VALUES(?,?,?,?,?,?)", arrayOf(i+1,u[0],u[1],PasswordHasher.hash(u[2]),u[3],now)) }
        listOf("Beverages","Snacks","Grocery","Personal Care").forEachIndexed { i,n -> db.execSQL("INSERT INTO categories(id,name,description) VALUES(?,?,?)",arrayOf(i+1,n,"")) }
        data class Seed(val name:String,val barcode:String,val price:Double,val cost:Double,val stock:Int,val min:Int,val cat:Int)
        val products = listOf(
            Seed("Beverage - Cola 1.5L","4801234567890",89.0,55.0,42,10,1),
            Seed("Snack - Potato Chips","4809876543210",45.0,28.0,8,10,2),
            Seed("Grocery - Rice 5kg","4805551234567",250.0,190.0,23,5,3),
            Seed("Personal Care - Shampoo","4802223334444",120.0,80.0,4,6,4),
            Seed("Beverage - Bottled Water","4803334445555",25.0,12.0,60,15,1),
            Seed("Snack - Chocolate Bar","4804445556666",35.0,20.0,3,8,2),
            Seed("Grocery - Cooking Oil 1L","4806667778888",95.0,65.0,18,5,3),
            Seed("Beverage - Energy Drink","4807778889999",55.0,35.0,27,8,1),
            Seed("Snack - Biscuits Pack","4808889990000",30.0,18.0,45,12,2),
            Seed("Grocery - Sugar 1kg","4809990001111",65.0,45.0,33,10,3))
        products.forEachIndexed { i,p -> db.execSQL("INSERT INTO products(id,name,barcode,price,cost,stock,minAlert,categoryId,createdAt) VALUES(?,?,?,?,?,?,?,?,?)",arrayOf(i+1,p.name,p.barcode,p.price,p.cost,p.stock,p.min,p.cat,now)) }
        // Stock above is the opening balance AFTER these historical example sales.
        repeat(6) { i ->
            val a=products[i]; val b=products[(i+4)%10]; val subtotal=rounded(a.price*2+b.price)
            val total=rounded(subtotal+rounded(subtotal*0.12)); val cost=rounded(a.cost*2+b.cost)
            db.execSQL("INSERT INTO transactions(id,date,time,total,items,payment,cashierName,cashierId,cost,profit,createdAt,manual,tendered) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",arrayOf(i+1,LocalDate.now().minusDays((5-i).toLong()).toString(),"10:30",total,3,"Cash","Juan Dela Cruz",2,cost,rounded(total-cost),now-(5-i)*86400000L,0,total))
            listOf(Triple(i,a,2),Triple((i+4)%10,b,1)).forEach { (index,p,q) -> db.execSQL("INSERT INTO line_items(transactionId,productId,productName,quantity,price,cost,categoryId) VALUES(?,?,?,?,?,?,?)",arrayOf(i+1,index+1,p.name,q,p.price,p.cost,p.cat)) }
        }
        db.execSQL("INSERT INTO batches(productId,supplierName,dateReceived,quantityReceived,costPerUnit,quantityRemaining) SELECT id,'Demo opening stock',createdAt-2592000000,stock,cost,stock FROM products WHERE stock>0")
        listOf(arrayOf("South Supermarket","Feliza Bldg, 110a MacArthur Hwy, Malolos, 3000 Bulacan; VR92+HX","",14.8689375,120.8024375),arrayOf("O!Save","RRX7+6M8 Rubrics Stone And Prostate Center Of Asia, Lites Marketing Building, Paseo del Congreso St, Malolos, 3000 Bulacan","",14.8480375,120.814203125),arrayOf("Alfamart","RRW8+72Q, Liang, Malolos, Bulacan","",14.8457125,120.815109375)).forEach { db.execSQL("INSERT INTO suppliers(name,address,ipAddress,latitude,longitude) VALUES(?,?,?,?,?)",it) }
        listOf(2 to "Juan Dela Cruz",3 to "Ana Reyes").forEach{(id,name)->repeat(2){n->val start=now-(n+1)*86400000L;db.execSQL("INSERT INTO shifts(cashierId,cashierName,timeIn,timeOut) VALUES(?,?,?,?)",arrayOf(id,name,start,start+28800000L))}}
        listOf("Login","Sale","Product Updated","Sale").forEachIndexed { i,a -> db.execSQL("INSERT INTO audit_log(timestamp,userName,action,details,type) VALUES(?,?,?,?,?)",arrayOf(now-i*60000,"Demo",a,"Seed example record",if(a=="Login") "auth" else if(a=="Sale") "sale" else "update")) }
    }
}
