package com.bizpulse.app.util
import com.bizpulse.app.data.db.entity.BatchEntity
object Fifo {
 fun allocate(batches:List<BatchEntity>,quantity:Int):List<Pair<BatchEntity,Int>> {
  require(quantity>0){"Quantity must be positive"};var remaining=quantity
  val result=mutableListOf<Pair<BatchEntity,Int>>()
  batches.sortedWith(compareBy<BatchEntity>{it.dateReceived}.thenBy{it.id}).forEach { b -> if(remaining>0 && b.quantityRemaining>0){val n=minOf(remaining,b.quantityRemaining);result+=b to n;remaining-=n} }
  require(remaining==0){"Insufficient unexpired batch stock"};return result
 }
}
