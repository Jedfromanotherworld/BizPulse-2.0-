package com.bizpulse.app.data.db
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
object Migrations {
 val V1_2=object:Migration(1,2){override fun migrate(db:SupportSQLiteDatabase){
 db.execSQL("CREATE TABLE IF NOT EXISTS batches (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, productId INTEGER NOT NULL, supplierName TEXT NOT NULL, dateReceived INTEGER NOT NULL, quantityReceived INTEGER NOT NULL, costPerUnit REAL NOT NULL, quantityRemaining INTEGER NOT NULL, expiryDate TEXT)")
 db.execSQL("CREATE INDEX IF NOT EXISTS index_batches_productId ON batches(productId)")
 db.execSQL("ALTER TABLE line_items ADD COLUMN batchId INTEGER")
 db.execSQL("ALTER TABLE line_items ADD COLUMN batchReceivedAt INTEGER")
 db.execSQL("INSERT INTO batches(productId,supplierName,dateReceived,quantityReceived,costPerUnit,quantityRemaining) SELECT id,'Opening stock',createdAt,stock,cost,stock FROM products WHERE stock>0")
 db.execSQL("UPDATE transactions SET payment='QR' WHERE payment IN ('Card','GCash')")
 }}
 val V2_3=object:Migration(2,3){override fun migrate(db:SupportSQLiteDatabase){db.execSQL("ALTER TABLE products ADD COLUMN imageUrl TEXT")}}
 val V3_4=object:Migration(3,4){override fun migrate(db:SupportSQLiteDatabase){
 db.execSQL("CREATE TABLE IF NOT EXISTS shifts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, cashierId INTEGER NOT NULL, cashierName TEXT NOT NULL, timeIn INTEGER NOT NULL, timeOut INTEGER)")
 db.execSQL("CREATE INDEX IF NOT EXISTS index_shifts_cashierId ON shifts(cashierId)")
 db.execSQL("ALTER TABLE transactions ADD COLUMN shiftId INTEGER")
 }}
 val V4_5=object:Migration(4,5){override fun migrate(db:SupportSQLiteDatabase){db.execSQL("CREATE TABLE IF NOT EXISTS suppliers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, address TEXT NOT NULL, ipAddress TEXT NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL)")}}
}
